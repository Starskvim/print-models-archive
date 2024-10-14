package com.starskvim.print.models.archive.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.starskvim.print.models.archive.domain.files.FolderService
import com.starskvim.print.models.archive.domain.model.PrintModelContext
import com.starskvim.print.models.archive.mapping.PrintModelContextMapper
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import mu.KLogging
import org.springframework.stereotype.Service
import java.io.File

@Service
class PrintModelContextService(
    private val dataService: PrintModelDataService,
    private val folderService: FolderService,
    private val mapper: PrintModelContextMapper,
    private val objectMapper: ObjectMapper
) {

    suspend fun saveContext(modelId: String, context: PrintModelContext) {
        var printModel = dataService.getPrintModelByIdRequired(modelId)
        val contextFile = File(printModel.path + "context.json")
        if (contextFile.exists()) {
            val existContext: PrintModelContext = objectMapper.readValue(contextFile)
            mapper.update(context, existContext)
            objectMapper.writeValue(contextFile, existContext)
        } else {
            objectMapper.writeValue(contextFile, context)
        }
    }

    suspend fun loadContext(model: PrintModelData): PrintModelContext? {
        //  TODO
        return null
    }

    companion object : KLogging()
}