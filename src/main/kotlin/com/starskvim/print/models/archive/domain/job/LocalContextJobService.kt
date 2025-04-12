package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.PrintModelLocalContextService
import com.starskvim.print.models.archive.persistance.PrintModelDataSearchService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.utils.WrapUtils.wrapException
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class LocalContextJobService(
    private val service: PrintModelLocalContextService,
    private val dataService: PrintModelDataService,
    private val searchService: PrintModelDataSearchService,
    private val config: GeminiClientConfiguration
) {

    // todo pages
    suspend fun process(): Int {
        var result = 0
        do {
            val models = searchService.getPrintModelsForMetaJob(
                limit = 10,
                ninProcessor = PROCESSOR_NAME,
                inProcessor = config.processorName // todo?
            )
            models.onEach {
                wrapException { service.saveContext(it) }
                it.getLazyMeta().processors.add(PROCESSOR_NAME)
                dataService.savePrintModel(it)
            }
            result += models.size
        } while (models.isNotEmpty())
        return result
    }

    suspend fun process(modelId: String) {
        val model = searchService.findById(modelId)
        if (model != null) {
            service.saveContext(model)
        }
    }

    companion object {
        val log = KLogging().logger()
        const val PROCESSOR_NAME = "LocalContextJob2"
    }

}