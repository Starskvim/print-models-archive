package com.starskvim.print.models.archive.persistance.repository

import com.starskvim.print.models.archive.persistance.model.setting.AppSettingsData
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface AppSettingsRepository : ReactiveMongoRepository<AppSettingsData, String> {
}