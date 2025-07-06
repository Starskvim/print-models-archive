package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.domain.meta.ImageMetaService.Companion.TOTAL_PROCESSOR_NAME
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

    suspend fun process(): Int {
        val models = searchService.getPrintModelsForMetaJob(
            limit = config.batchSize,
            ninProcessor = TOTAL_PROCESSOR_NAME
        )
        val ops = models.map { WrapUtils.wrapException(it) { imageMetaService.createImageMeta(it) } }
        ops.onEach { it.onException { imageMetaService.createFailImageMeta(it.source!!, it.exception!!) } }
        ops.filter { it.isNoSuccess() }.forEach { logger.info { "Model - ${it.source?.modelName} - ${it.exception}" } }
        return ops.size
    }

    // gemini-1.5-flash-latest_FAIL
    // gemini-2.0-flash_FAIL
    suspend fun processRetry(): Int {
        val firstModel = "gemini-1.5-flash-latest_FAIL"
        val secondModel = "gemini-2.0-flash_FAIL"
        var size = retry(firstModel)
        if (size == 0) {
            logger.info { "SecondModel start" }
            size = retry(secondModel)
        }
        return size;
    }

    private suspend fun retry(inProcessor: String): Int {
        return searchService.getPrintModelsForMetaJob(
            limit = config.batchSize,
            inProcessor = inProcessor
        )
            .map { WrapUtils.wrapException(it) { imageMetaService.createRetryMeta(it, inProcessor) } }
            .onEach { it.onException { imageMetaService.createFailImageMeta(it.source!!, it.exception!!) } }
            .size
    }

    companion object : KLogging()
}