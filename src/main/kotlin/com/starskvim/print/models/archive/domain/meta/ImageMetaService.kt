package com.starskvim.print.models.archive.domain.meta

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiApiException
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiImageTagService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.meta.ImageMeta
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ImageMetaService(
    private val taggingService: GeminiImageTagService,
    private val dataService: PrintModelDataService,
    private val config: GeminiClientConfiguration
) {

    suspend fun createMetaById(modelId: String) {
        dataService.getPrintModelById(modelId)?.let {
            createMeta(it)
        }
    }

    // exist gemini-1.5-flash-latest
    // gemini-2.0-flash
    suspend fun createMeta(model: PrintModelData) {
        val imageMeta = generateSingleImageMeta(model)
        model.getLazyMeta().apply {
            images.add(imageMeta)
            processors.add(TOTAL_PROCESSOR_NAME)
            processors.add(config.processorName)
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJob: for [${model.modelName}] meta added, tags size [${imageMeta.tags.size}]" }
    }

    // TODO
    // exist gemini-1.5-flash-latest_FAIL
    // gemini-2.0-flash_FAIL
    suspend fun createFailMeta(model: PrintModelData, ex: Exception) {
        if (ex is GeminiApiException && ex.statusCode.is4xxClientError) {
            logger.info { "ImageAiMetaJob: for [${model.modelName}] FAIL 400]" }
        }
        model.getLazyMeta().apply {
            processors.add(TOTAL_PROCESSOR_NAME)
            processors.add(config.processorName)
            processors.add(config.processorName + "_FAIL")
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJob: for [${model.modelName}] FAIL meta added]" }
    }

    suspend fun createRetryMeta(model: PrintModelData, inProcessor: String) {
        val imageMeta = generateSingleImageMeta(model)
        model.getLazyMeta().apply {
            images.add(imageMeta)
            processors = processors.filter { it != inProcessor }.toMutableSet()
            processors.add(TOTAL_PROCESSOR_NAME)
            processors.add(config.processorName)
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJobRetry: for [${model.modelName}] meta added, tags size [${imageMeta.tags.size}]" }
    }

    private suspend fun generateSingleImageMeta(model: PrintModelData): ImageMeta {
        val targetImage = model.oths
            ?.find { it.storageName == model.preview }
        val tags = targetImage
            ?.path
            ?.let { clearTags(taggingService.generateTags(it, model.modelName)) }
        return ImageMeta(
            fileName = targetImage?.fileName ?: "",
            processor = config.processorName,
            tags = tags ?: listOf()
        )
    }

    private suspend fun clearTags(responseTags: List<String>): List<String> {
        return responseTags.filter { !config.excludeTags.contains(it) }
    }

    companion object {
        val logger = KLogging().logger()
        const val TOTAL_PROCESSOR_NAME = "ImageMetaServiceJob"
    }
}