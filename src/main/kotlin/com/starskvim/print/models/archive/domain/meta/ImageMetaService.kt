package com.starskvim.print.models.archive.domain.meta

import com.starskvim.print.models.archive.config.ai.GeminiClientConfigurationProperties
import com.starskvim.print.models.archive.config.ai.OpenRouterConfigurationProperties
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiApiException
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiImageTagService
import com.starskvim.print.models.archive.domain.meta.gemini.GeminiLimitRequestException
import com.starskvim.print.models.archive.domain.meta.openrouter.OpenRouterService
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.meta.ImageMeta
import mu.KLogging
import org.apache.commons.collections4.CollectionUtils.isEmpty
import org.springframework.stereotype.Service

@Service
class ImageMetaService(
    private val geminiImageTagService: GeminiImageTagService,
    private val oImageTagService: OpenRouterService,
    private val dataService: PrintModelDataService,
    private val gConfig: GeminiClientConfigurationProperties,
    private val oConfig: OpenRouterConfigurationProperties
) {

    suspend fun createMetaById(modelId: String) {
        dataService.getPrintModelById(modelId)?.let {
            createImageMeta(it)
        }
    }

    suspend fun createImageMeta(model: PrintModelData) {
        val imagesMeta = generateImagesMeta(model)
        val tagsCount = imagesMeta.map { it.tags.size }.reduce { a, b -> a + b }
        model.getLazyMeta().apply {
            images.addAll(imagesMeta)
            processors.add(TOTAL_PROCESSOR_NAME)
            processors.add(oConfig.model)
        }
        dataService.savePrintModel(model)
        logger.info {
            "ImageAiMetaJob: for [${model.modelName}] meta added, imagesMeta [${imagesMeta.size}], tags size [${tagsCount}]"
        }
    }

    // TODO
    // exist gemini-1.5-flash-latest_FAIL
    // gemini-2.0-flash_FAIL
    suspend fun createFailImageMeta(model: PrintModelData, ex: Exception) {
        if (ex is GeminiApiException && (ex.statusCode.is4xxClientError || ex.statusCode.is5xxServerError)) {
            logger.info { "ImageAiMetaJob: FAIL 400/500 RETURN]" }
            return
        }
        if (ex is GeminiLimitRequestException) {
            logger.info { "ImageAiMetaJob: GeminiLimitRequestException FAIL 400/500 RETURN models: ${gConfig.getModelStats()}" }
            return
        }
        model.getLazyMeta().apply {
            processors.add(TOTAL_PROCESSOR_NAME)
            processors.add(oConfig.model)
            processors.add(oConfig.model + "_FAIL")
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJob: for [${model.modelName}] FAIL meta added]" }
    }

    suspend fun clearMeta(model: PrintModelData) {
        model.getLazyMeta().apply {
            images.clear()
            processors.clear()
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJobRetryClear: for [${model.modelName}] meta cleared" }
    }

    private suspend fun generateImagesMeta(model: PrintModelData): List<ImageMeta> {
        if (isEmpty(model.oths)) {
            return emptyList()
        }
        var count = 0
        val meta = mutableListOf<ImageMeta>()
        for (targetImage in model.oths!!) {
            if (count >= oConfig.imagePerPrintModel) {
                break
            }
            val tags = targetImage.path?.let {
                clearTags(
                    //geminiImageTagService.generateTags(it, model.modelName))
                    oImageTagService.generateTags(it, model.modelName)
                )
            }
            meta.add(
                ImageMeta(
                    fileName = targetImage.fileName ?: "",
                    processor = oConfig.model,
                    tags = tags ?: listOf()
                )
            )
            count++
        }
        return meta
    }

    private suspend fun clearTags(responseTags: List<String>): List<String> {
        return responseTags.filter { !gConfig.excludeTags.contains(it) }
    }

    companion object {
        val logger = KLogging().logger()
        const val TOTAL_PROCESSOR_NAME = "ImageMetaServiceJob_v2"
    }
}