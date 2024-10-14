package com.starskvim.print.models.archive.persistance

import com.mongodb.client.result.DeleteResult
import com.starskvim.print.models.archive.aop.LoggTime
import com.starskvim.print.models.archive.persistance.model.print_model.PrintModelData
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.utils.Constants.Document.PRINT_MODELS
import com.starskvim.print.models.archive.utils.Constants.Fields.FOLDER_NAME
import com.starskvim.print.models.archive.utils.Constants.Logs.UN_ER
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.bson.Document
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.GroupOperation
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import ru.starskvim.inrastructure.webflux.advice.exception.NotFoundException


@Service
class PrintModelDataService(
    private val template: ReactiveMongoTemplate,
    private val searchDataService: PrintModelDataSearchService
) {

    suspend fun savePrintModel(model: PrintModelData): PrintModelData? = template.save(model)
        .awaitSingleOrNull()

    suspend fun saveAll(models: Collection<PrintModelData>) {
        template.insertAll(models)
            .awaitFirstOrNull()
    }

    @LoggTime
    suspend fun deleteAll(): DeleteResult = template.remove(Query(), PRINT_MODELS).awaitSingle()

    suspend fun getPrintModels(
        searchParams: PrintModelSearchParams,
        pageable: Pageable
    ): Page<PrintModelData> {
        return searchDataService.getPrintModelsPage(
            searchParams,
            pageable
        )
    }

    suspend fun getPrintModelById(modelId: String): PrintModelData? {
        return searchDataService.findById(modelId)
    }

    suspend fun getPrintModelByIdRequired(modelId: String): PrintModelData {
        return searchDataService.findById(modelId)
            ?: throw NotFoundException("$UN_ER Not found for download id $modelId")
    }

    suspend fun resolveAllExistModelNames(): MutableSet<String> {
        val groupOperation: GroupOperation = Aggregation.group()
            .addToSet(FOLDER_NAME)
            .`as`(FOLDER_NAME)
        val aggregation: Aggregation = Aggregation.newAggregation(groupOperation)
        val aggregationResults = template.aggregate(
            aggregation,
            PRINT_MODELS,
            Document::class.java
        ).awaitFirstOrNull() ?: return mutableSetOf()
        return aggregationResults.getList(FOLDER_NAME, String::class.java)
            .toMutableSet()
    }
}