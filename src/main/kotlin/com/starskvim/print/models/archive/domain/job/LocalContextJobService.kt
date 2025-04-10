package com.starskvim.print.models.archive.domain.job

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
    private val searchService: PrintModelDataSearchService
) {

    suspend fun process() {
        searchService.getPrintModelsForMetaJob(
            limit = 10,
            ninProcessor = "LocalContextJob"
        ) // todo
            .onEach {
                wrapException { service.saveContext(it) }
                it.getLazyMeta().processors.add("LocalContextJob")
                dataService.savePrintModel(it)
            }
    }

    suspend fun process(modelId: String) {
        val model = searchService.findById(modelId)
        if (model != null) {
            service.saveContext(model)
        }
    }

    companion object : KLogging()

}