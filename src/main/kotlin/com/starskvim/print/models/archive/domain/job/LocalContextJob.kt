package com.starskvim.print.models.archive.domain.job

import jakarta.annotation.PostConstruct
import mu.KLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("local-context.job", havingValue = "true")
class LocalContextJob(
    private val service: LocalContextJobService
) {

    @PostConstruct
    fun init() {
        logger.info { "LocalContextJob init." }
    }

    suspend fun process() {
        logger.info { "LocalContextJob started" }
        service.process()
        logger.info { "LocalContextJob finished" }
    }
    companion object : KLogging()
}