package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.persistance.PrintModelDataSearchService
import com.starskvim.print.models.archive.utils.WrapUtils
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ImageAiMetaJobService(
    private val searchService: PrintModelDataSearchService,
    private val imageMetaService: ImageMetaService,
    private val config: GeminiClientConfiguration
) {

    suspend fun process() {
        searchService.getPrintModelsForMetaJob(config.batchSize, config.processorName)
            .forEach { WrapUtils.wrapException { imageMetaService.createMeta(it) } } // todo logs
    }

    companion object : KLogging()
}