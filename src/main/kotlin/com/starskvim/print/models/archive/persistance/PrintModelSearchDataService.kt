package com.starskvim.print.models.archive.persistance

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.utils.Constants.Fields.CATEGORIES
import com.starskvim.print.models.archive.utils.Constants.Fields.MODEL_NAME
import com.starskvim.print.models.archive.utils.Constants.Fields.OTHS
import com.starskvim.print.models.archive.utils.Constants.Fields.RATE
import com.starskvim.print.models.archive.utils.Constants.Fields.ZIPS
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.types.ObjectId
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

    suspend fun findById(modelId: String): PrintModelData? {
        return template.findById(ObjectId(modelId), PrintModelData::class.java).awaitFirstOrNull()
    }

    suspend fun getPrintModelsPage(
        searchParams: PrintModelSearchParams,
        pageable: Pageable
    ): Page<PrintModelData> {
        val query = Query()
        addIsLikeCriteria(query, MODEL_NAME, searchParams.modelName)
        addInCriteria(query, MODEL_NAME, searchParams.modelNames)
        addInCriteria(query, CATEGORIES, searchParams.category)
        addGteCriteria(query, RATE, searchParams.rate)
        addExcludeFieldsCriteria(query, ZIPS, OTHS)
        val totalCount = template.count(query, PrintModelData::class.java).awaitSingleOrNull()
        query.with(pageable)
        val result = template.find(query, PrintModelData::class.java).collectList().awaitSingleOrNull()
        return PageImpl(result?.toList() ?: emptyList(), pageable, totalCount ?: 0)
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
