package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.persistance.model.CategoriesInfoData
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service

@Service
class CategoriesInfoDataService(

    private val mongoTemplate: ReactiveMongoTemplate

) {

    suspend fun saveCategoriesInfo(categoriesInfo: CategoriesInfoData): CategoriesInfoData {
        return mongoTemplate.save(categoriesInfo).awaitSingle()
    }

    suspend fun getAllCategories(): List<String> {
        val result = mongoTemplate.findAll(CategoriesInfoData::class.java)
            .collectList()
            .awaitSingle() ?: emptyList<CategoriesInfoData>()
        return result[0].categories ?: emptyList()
    }
}