package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.domain.setting.AppSettingsService
import jakarta.annotation.PostConstruct
import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("google.gemini.job", havingValue = "true")
class ImageAiMetaJob(
    private val service: ImageAiMetaJobService,
    private val settings: AppSettingsService
) {

    @PostConstruct
    fun init() {
        logger.info { "ImageAiMetaJob init..." }
    }

    @Scheduled(cron = "\${google.gemini.cron}")
    suspend fun process() {
        if (settings.getAppSettings().imageAiMetaJob) {
            logger.info { "ImageAiMetaJob started" }
            val processed = service.process()
            logger.info { "ImageAiMetaJob finished. Processed [$processed]" }
        } else {
            logger.info { "ImageAiMetaJob disabled" }
        }
    }

    companion object : KLogging()
}