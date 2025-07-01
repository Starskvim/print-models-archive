package com.starskvim.print.models.archive.domain.setting

import com.starskvim.print.models.archive.persistance.model.setting.AppSettingsData
import com.starskvim.print.models.archive.persistance.repository.AppSettingsRepository
import com.starskvim.print.models.archive.utils.Constants
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class AppSettingsService(
    val repository: AppSettingsRepository
) {

    @PostConstruct
    fun init() {
        logger.info { "AppSettingsService init..." }
        runBlocking {
            val s = getAppSettings()
            logger.info {
                """AppSettings loaded successfully: 
                |[imageAiMetaJob - ${s.imageAiMetaJob}]"""
                    .trimMargin()
            }
        }
    }

    suspend fun getAppSettings(): AppSettingsData {
        return repository.findById(Constants.Data.APP_SETTINGS_ID).awaitSingleOrNull()
            ?: initSettings()
    }

    private suspend fun initSettings(): AppSettingsData {
        return repository.save(
            AppSettingsData(
                Constants.Data.APP_SETTINGS_ID,
                imageAiMetaJob = false
            )
        ).awaitSingle()
    }

    companion object : KLogging()
}