package com.starskvim.print.models.archive.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.starskvim.print.models.archive.domain.model.PrintModelContext
import com.starskvim.print.models.archive.mapping.PrintModelContextMapper
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import mu.KLogging
import org.springframework.stereotype.Service
import java.io.File

@Service
class PrintModelLocalContextService(
    private val mapper: PrintModelContextMapper,
    private val objectMapper: ObjectMapper
) {

    suspend fun saveContext (model: PrintModelData) {
        saveContext(model.path!!, PrintModelContext(
            0,
            null,
            model.getLazyMeta()
        ))
        logger.info { "Local context saved for ${model.modelName}" }
    }

    suspend fun saveContext(modelPath: String, context: PrintModelContext) {
        val contextFile = File(modelPath + "\\" + "context.json")
        if (contextFile.exists()) {
            val existContext: PrintModelContext = objectMapper.readValue(contextFile)
            context.apply {
                version = existContext.version!! + 1
            }
            mapper.update(context, existContext)
            objectMapper.writeValue(contextFile, existContext)
        } else {
            objectMapper.writeValue(contextFile, context)
        }
    }

    suspend fun loadContext(modelPath: String): PrintModelContext? {
        val contextFile = File(modelPath + "context.json")
        if (contextFile.exists()) {
            return objectMapper.readValue(contextFile)
        }
        return null
    }

    companion object : KLogging()
}