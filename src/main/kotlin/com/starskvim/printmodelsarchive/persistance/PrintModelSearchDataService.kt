package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import com.starskvim.printmodelsarchive.utils.Constants.Fields.CATEGORIES
import com.starskvim.printmodelsarchive.utils.Constants.Fields.MODEL_NAME
import com.starskvim.printmodelsarchive.utils.Constants.Fields.OTHS
import com.starskvim.printmodelsarchive.utils.Constants.Fields.ZIPS
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class PrintModelSearchDataService(

    private val template: ReactiveMongoTemplate

) : SearchMongoService {

    suspend fun findById(id: String): PrintModelData? =
        template.findById(id, PrintModelData::class.java).awaitSingleOrNull()

    suspend fun getPrintModelsPage(searchParams: PrintModelSearchParams, pageable: Pageable): Page<PrintModelData> {
        val query = Query()
        addIsLikeCriteria(query, MODEL_NAME, searchParams.modelName)
        addInCriteria(query, CATEGORIES, searchParams.category)
        addExcludeFieldsCriteria(query, ZIPS, OTHS)
        query.with(pageable)
        val result = template.find(query, PrintModelData::class.java).collectList().awaitSingleOrNull()
        return createPage(result?.toList() ?: emptyList(), query, pageable)
    }

    private suspend fun createPage(
        result: List<PrintModelData>,
        query: Query,
        pageable: Pageable
    ): Page<PrintModelData> {
        val totalCount = template.count(query, PrintModelData::class.java).awaitSingleOrNull()
        return PageImpl(result, pageable, totalCount ?: 0)
    }
}