package com.starskvim.print.models.archive.domain.job

import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("google.gemini.job", havingValue = "true")
class ImageAiMetaJob(
    private val service: ImageAiMetaJobService
) {

    @Scheduled(cron = "\${google.gemini.cron}")
    suspend fun process() {
        logger.info { "ImageAiMetaJob started" }
        service.process()
        logger.info { "ImageAiMetaJob finished" }
    }

    companion object : KLogging()
}