package com.starskvim.print.models.archive.persistance

import com.mongodb.client.result.DeleteResult
import com.starskvim.print.models.archive.aop.LoggTime
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.utils.Constants.Document.PRINT_MODELS
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service

@Service
class PrintModelDataService(
    private val template: ReactiveMongoTemplate,
    private val searchDataService: PrintModelSearchDataService
) {

    @LoggTime
    suspend fun savePrintModel(model: PrintModelData): PrintModelData? = template.save(model)
        .awaitSingleOrNull()

    @LoggTime
    suspend fun saveAll(models: Collection<PrintModelData>) {
        template.insertAll(models)
            .awaitFirstOrNull()
    }

    @LoggTime
    suspend fun deleteAll(): DeleteResult = template.remove(Query(), PRINT_MODELS).awaitSingle()

    suspend fun getPrintModels(searchParams: PrintModelSearchParams, pageable: Pageable): Page<PrintModelData> {
        return searchDataService.getPrintModelsPage(searchParams, pageable)
    }

    suspend fun getPrintModelById(modelId: String): PrintModelData? {
        return searchDataService.findById(modelId)
    }
}