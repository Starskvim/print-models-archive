package com.starskvim.print.models.archive.domain.job

import jakarta.annotation.PostConstruct
import mu.KLogging
import org.springframework.stereotype.Component

@Component
class ImageAiMetaRetryJob (
    private val service: ImageAiMetaJobService
) {

    @PostConstruct
    fun init() {
        logger.info { "ImageAiMetaRetryJob init." }
    }

    suspend fun process() {
        logger.info { "ImageAiMetaRetryJob started" }
        val processed = service.processRetry()
        logger.info { "ImageAiMetaRetryJob finished. Processed [$processed]" }
    }

    companion object : KLogging()
}