package com.starskvim.print.models.archive.persistance.repository

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface PrintModelRepository : ReactiveMongoRepository<PrintModelData, String> {
}