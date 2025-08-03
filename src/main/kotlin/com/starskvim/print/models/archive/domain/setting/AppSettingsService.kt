package com.starskvim.print.models.archive.domain.setting

import com.starskvim.print.models.archive.mapping.AppSettingsMapper
import com.starskvim.print.models.archive.persistance.model.setting.AppSettingsData
import com.starskvim.print.models.archive.persistance.repository.AppSettingsRepository
import com.starskvim.print.models.archive.utils.Constants
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

@Service
class AppSettingsService(
    val repository: AppSettingsRepository,
    val mapper: AppSettingsMapper
) {

    @PostConstruct
    fun init() {
        logger.info { "AppSettingsService init..." }
        runBlocking {
            val s = getAppSettings()
            mapper.update(s, default())
            repository.save(s).awaitSingleOrNull()
            logger.info {
                """AppSettings loaded successfully: 
                |[imageAiMetaJob - ${s.imageAiMetaJob}]
                |[commonBatchSize - ${s.commonBatchSize}]
                |[openRouterApiKey - ${mask(s.openRouterApiKey)}]
                |[geminiApiKey - ${mask(s.geminiApiKey)}]"""
                    .trimMargin()
            }
        }
    }

    suspend fun getAppSettings(): AppSettingsData {
        return repository.findById(Constants.Data.APP_SETTINGS_ID).awaitSingleOrNull()
            ?: initSettings()
    }

    private suspend fun initSettings(): AppSettingsData {
        return repository.save(default()).awaitSingle()
    }

    private suspend fun default(): AppSettingsData {
        return AppSettingsData(
            Constants.Data.APP_SETTINGS_ID,
            commonBatchSize = 10,
            imageAiMetaJob = false,
            imageAiMetaClearJob = false
        )
    }

    private fun mask(s: String): String {
        return if (StringUtils.isBlank(s)) {
            "blank"
        } else {
            "${s[0]} + ${s.hashCode()}"
        }
    }

    companion object : KLogging()
}