package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.aop.LoggTime
import com.starskvim.printmodelsarchive.persistance.model.PrintModelData
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
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
    suspend fun saveAll(models: Collection<PrintModelData>): Collection<PrintModelData>? = template.insertAll(models)
        .collectList()
        .awaitFirstOrNull()

    suspend fun getPrintModels(searchParams: PrintModelSearchParams, pageable: Pageable): Page<PrintModelData> {
        return searchDataService.getPrintModels(searchParams, pageable)
    }
}