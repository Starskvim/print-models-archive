package com.starskvim.printmodelsarchive.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfiguration {

    @Bean
    fun createObjectMapperBean(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        return mapper
    }
}