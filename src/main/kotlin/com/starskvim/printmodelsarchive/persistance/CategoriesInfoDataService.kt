package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.persistance.model.CategoriesInfoData
import com.starskvim.printmodelsarchive.rest.model.Category
import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import kotlinx.coroutines.reactive.awaitFirstOrNull
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

    suspend fun getAllCategories(): List<Category> {
        return mongoTemplate.findById(CATEGORIES_INFO, CategoriesInfoData::class.java)
            .awaitFirstOrNull()
            ?.countInfo
            ?.map { Category(it.key, it.value) }
            ?.sortedBy { it.name }
            ?: return emptyList()

    }
}
