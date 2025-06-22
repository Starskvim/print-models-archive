package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.context.PrintModelLocalContextService
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

    suspend fun process(): Int {
        var result = 0
        do {
            val models = searchService.getPrintModelsForMetaJob(
                limit = 10,
                ninProcessor = LOCAL_CONTEXT_PROCESSOR_NAME
            )
            log.info { "LocalContextJob current batch [${models.size}]" }
            models.onEach {
                val processors = it.getLazyMeta().processors
                val op = wrapException { service.saveContext(it) }
                op.onException {
                    processors.add(LOCAL_CONTEXT_PROCESSOR_NAME + "_FAIL")
                    log.info { "LocalContextJob exception with [${it.modelName}], ex: ${op.exception}" }
                }
                processors.add(LOCAL_CONTEXT_PROCESSOR_NAME)
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
        const val LOCAL_CONTEXT_PROCESSOR_NAME = "LocalContextJob_1"
    }

}