package com.starskvim.print.models.archive.domain.meta

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
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

    suspend fun createMeta(model: PrintModelData) {
        val imageMeta = generateSingleImageMeta(model)
        model.getLazyMeta().apply {
            images.add(imageMeta)
            processors.add(config.processorName)
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJob: for [${model.modelName}] meta added, tags size [${imageMeta.tags.size}]" }
    }

    suspend fun generateSingleImageMeta(model: PrintModelData): ImageMeta {
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

    suspend fun clearTags(responseTags: List<String>): List<String> {
        return responseTags.filter { !config.excludeTags.contains(it) }
    }

    companion object : KLogging()
}