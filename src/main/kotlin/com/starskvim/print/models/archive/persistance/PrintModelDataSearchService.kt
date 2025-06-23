package com.starskvim.print.models.archive.persistance

import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.utils.Constants.Fields.CATEGORIES
import com.starskvim.print.models.archive.utils.Constants.Fields.META_PROCESSORS
import com.starskvim.print.models.archive.utils.Constants.Fields.MODEL_NAME
import com.starskvim.print.models.archive.utils.Constants.Fields.NSFW
import com.starskvim.print.models.archive.utils.Constants.Fields.OTHS
import com.starskvim.print.models.archive.utils.Constants.Fields.RATE
import com.starskvim.print.models.archive.utils.Constants.Fields.ZIPS
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class PrintModelDataSearchService(
    private val template: ReactiveMongoTemplate
) : MongoSearchService {

    suspend fun findById(modelId: String): PrintModelData? {
        return template.findById(modelId, PrintModelData::class.java).awaitFirstOrNull()
    }

    suspend fun getPrintModelsPage(
        searchParams: PrintModelSearchParams,
        pageable: Pageable,
        needCount: Boolean = true
    ): Page<PrintModelData> {
        val query = Query()
        addIsLikeCriteria(query, MODEL_NAME, searchParams.modelName)
        addInCriteria(query, MODEL_NAME, searchParams.modelNames)
        query.addInCriteria( CATEGORIES, searchParams.category)
        addIsCriteria(query, NSFW, searchParams.nsfwOnly)
        addGteCriteria(query, RATE, searchParams.rate)
        addExcludeFieldsCriteria(query, ZIPS, OTHS)
        val totalCount = when (needCount) {
            true -> template.count(query, PrintModelData::class.java).awaitSingleOrNull()
            false -> null
        }
        query.with(pageable)
        val result = template.find(query, PrintModelData::class.java).collectList().awaitSingleOrNull()
        return PageImpl(result?.toList() ?: emptyList(), pageable, totalCount ?: 0)
    }

    suspend fun getPrintModelsForMetaJob(
        limit: Int,
        ninProcessor: String? = null,
        inProcessor: String? = null
    ): List<PrintModelData> {
        val query = Query()
        query.addNinCriteria(META_PROCESSORS, ninProcessor)
        query.addInCriteria(META_PROCESSORS, inProcessor)
        query.limit(limit)
        return template.find(query, PrintModelData::class.java)
            .collectList()
            .awaitSingleOrNull()
            ?: emptyList()
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
