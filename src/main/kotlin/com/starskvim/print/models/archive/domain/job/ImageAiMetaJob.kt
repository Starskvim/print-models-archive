package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.config.ai.GeminiClientConfiguration
import com.starskvim.print.models.archive.domain.meta.ImageMetaService
import com.starskvim.print.models.archive.persistance.PrintModelDataSearchService
import com.starskvim.print.models.archive.utils.WrapUtils.wrapException
import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("google.gemini.job", havingValue = "true")
class ImageAiMetaJob(
    private val searchService: PrintModelDataSearchService,
    private val imageMetaService: ImageMetaService,
    private val config: GeminiClientConfiguration
) {

    @Scheduled(cron = "\${google.gemini.cron}")
    suspend fun process() {
        logger.info { "ImageAiMetaJob started" }
        searchService.getPrintModelsForJob(config.batchSize)
            .forEach { wrapException { imageMetaService.createMeta(it) } } // todo logs
        logger.info { "ImageAiMetaJob finished" }
    }

    companion object : KLogging()
}