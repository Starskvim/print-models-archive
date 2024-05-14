package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.mapping.PrintModelMapper
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.persistance.PrintModelSearchDataService
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModel
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModelSuggest
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import com.starskvim.print.models.archive.rest.model.response.PrintModelCardsResponse
import com.starskvim.print.models.archive.utils.Exceptions.MODEL_NOT_FOUND
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import ru.starskvim.inrastructure.webflux.advice.exception.NotFoundException

@Service
class PrintModelService(
    private val dataService: PrintModelDataService,
    private val searchDataService: PrintModelSearchDataService,
    private val mapper: PrintModelMapper
) {

    // TODO
    suspend fun getPrintModelsPageApi(
        searchParams: PrintModelSearchParams,
        pageable: Pageable
    ): PrintModelCardsResponse {
        val dataPage = searchDataService.getPrintModelsPage(
            searchParams,
            pageable
        )
        return PrintModelCardsResponse(
            models = dataPage.content.map { mapper.dataToCardApi(it) },
            totalPages = dataPage.totalPages,
            totalElements = dataPage.totalElements
        )
    }

    suspend fun getSuggestionModels(
        query: String
    ): List<PrintModelSuggest> {
        val dataPage = searchDataService.getPrintModelsPage(
            PrintModelSearchParams(modelName = query),
            Pageable.ofSize(3),
            needCount = false
        )
        return mapper.dataToSuggestApi(dataPage.content)
    }

    suspend fun getPrintModelsPage(
        searchParams: PrintModelSearchParams,
        pageable: Pageable
    ): Page<PrintModel> {
        val dataPage = searchDataService.getPrintModelsPage(searchParams, pageable)
        return PageImpl(mapper.dataToApi(dataPage.content), pageable, dataPage.totalElements)
    }

    suspend fun getPrintModelById(
        modelId: String
    ): PrintModel {
        val model = dataService.getPrintModelById(modelId)
        return mapper.dataToApi(model)
            ?: throw NotFoundException(MODEL_NOT_FOUND)
    }
}
