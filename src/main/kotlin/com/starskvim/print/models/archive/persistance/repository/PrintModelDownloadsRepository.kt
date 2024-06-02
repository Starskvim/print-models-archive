package com.starskvim.print.models.archive.persistance.repository

import com.starskvim.print.models.archive.persistance.model.downloads.PrintModelDownloads
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PrintModelDownloadsRepository : ReactiveMongoRepository<PrintModelDownloads, String> {
}