package com.starskvim.printmodelsarchive.config.mongo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.starskvim.printmodelsarchive.config.mongo.converter.LocalDateReadConverter
import com.starskvim.printmodelsarchive.config.mongo.converter.LocalDateWriteConverter
import org.bson.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoDbConfiguration(
    val mapper: ObjectMapper
) {

    @Bean
    fun createMongoDbCustomConversion(): MongoCustomConversions {
        return MongoCustomConversions(
            mutableListOf(
                JsonNodeToDocumentConverter(),
                DocumentToJsonNodeConverter(),
                LocalDateReadConverter(),
                LocalDateWriteConverter()
            )
        )
    }

    @WritingConverter
    inner class JsonNodeToDocumentConverter() : Converter<JsonNode, Document> {
        override fun convert(source: JsonNode): Document? {
            clearNullJsonNode(source)
            return Document.parse(mapper.writeValueAsString(source))
        }

        private fun clearNullJsonNode(source: JsonNode) {
            if (source.isArray) {
                for (element in source) {
                    clearNullJsonNode(element)
                }
            } else if (source.isContainerNode) {
                val fields = source.fields()
                fields.forEachRemaining {
                    val value = it.value
                    if (value == null) fields.remove()
                    else if (value.isArray || value.isContainerNode) clearNullJsonNode(value)
                }
            }
        }
    }

    @ReadingConverter
    inner class DocumentToJsonNodeConverter : Converter<Document, JsonNode> {
        override fun convert(source: Document): JsonNode? {
            return this@MongoDbConfiguration.mapper.readTree(source.toJson())
        }
    }
}