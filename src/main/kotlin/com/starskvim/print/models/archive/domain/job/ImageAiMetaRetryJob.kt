package com.starskvim.print.models.archive.domain.job

import com.starskvim.print.models.archive.domain.setting.AppSettingsService
import jakarta.annotation.PostConstruct
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class ImageAiMetaRetryJob (
    private val service: ImageAiMetaJobService,
    private val settings: AppSettingsService
) {

    @PostConstruct
    fun init() {
        logger.info { "ImageAiMetaRetryJob init." }
    }

    suspend fun process() {
        if (settings.getAppSettings().imageAiMetaClearJob) {
            logger.info { "ImageAiMetaRetryJobClear started" }
            val processed = service.processRetryByClear(settings.getAppSettings().commonBatchSize)
            logger.info { "ImageAiMetaRetryJobClear finished. Processed [$processed]" }
        } else {
            logger.info { "ImageAiMetaRetryJobClear disabled" }
        }
    }

    companion object : KLogging()
}