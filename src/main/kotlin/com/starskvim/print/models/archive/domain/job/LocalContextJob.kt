package com.starskvim.print.models.archive.domain.job

import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("local-context.job", havingValue = "true")
class LocalContextJob(
    private val service: LocalContextJobService
) {

    suspend fun process() {
        logger.info { "LocalContextJob started" }
        service.process()
        logger.info { "LocalContextJob finished" }
    }
    companion object : KLogging()
}