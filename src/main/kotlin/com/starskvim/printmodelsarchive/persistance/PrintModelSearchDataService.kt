package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service

@Service
class PrintModelSearchDataService(

    private val template : ReactiveMongoTemplate

) : SearchMongoService {

    suspend fun findById(id : String) : PrintModelData? = template.findById(id, PrintModelData::class.java).awaitSingleOrNull()
}