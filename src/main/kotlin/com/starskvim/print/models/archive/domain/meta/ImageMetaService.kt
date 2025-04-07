package com.starskvim.print.models.archive.domain.meta

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.persistance.model.print_model.meta.ImageMeta
import com.starskvim.print.models.archive.persistance.model.print_model.meta.Meta
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ImageMetaService(
    private val taggingService: ImageTaggingService,
    private val dataService: PrintModelDataService,
    private val config: GeminiClientConfiguration
) {

    suspend fun createMetaById(modelId: String) {
        dataService.getPrintModelById(modelId)?.let {
            createMeta(it)
        }
    }

    suspend fun createMeta(model: PrintModelData) {
        val targetImage = model.oths
            ?.find { it.path == model.preview }
            ?: return

        val tags = targetImage
            .path
            ?.let { taggingService.generateTags(it) }
            ?: return

        model.apply {// todo add logic
            meta = Meta(
                listOf(ImageMeta(targetImage.fileName!!, config.processorName, tags)),
                listOf(config.processorName)
            )
        }
        dataService.savePrintModel(model)
        logger.info { "ImageAiMetaJob: for ${model.modelName} meta added" }
    }

    companion object : KLogging()
}