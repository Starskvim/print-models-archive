package com.starskvim.printmodelsarchive.persistance

import com.mongodb.client.result.DeleteResult
import com.starskvim.printmodelsarchive.persistance.model.catalog.CategoriesInfoData
import com.starskvim.printmodelsarchive.rest.model.Category
import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class CategoriesInfoDataService(
    private val template: ReactiveMongoTemplate
) {

    suspend fun saveCategoriesInfo(categoriesInfo: CategoriesInfoData): CategoriesInfoData {
        return template.save(categoriesInfo).awaitSingle()
    }

    suspend fun getAllCategories(): List<Category> {
        return template.findById(CATEGORIES_INFO, CategoriesInfoData::class.java)
            .awaitFirstOrNull()
            ?.categoriesCatalog
            ?.catalog
            ?.map { Category(it.name, it.size) }
            ?.sortedBy { it.name }
            ?: return emptyList()

    }

    suspend fun deleteAll(): DeleteResult = template.remove(Query(), CATEGORIES_INFO).awaitSingle()
}
