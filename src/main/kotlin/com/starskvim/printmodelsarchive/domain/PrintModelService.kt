package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.mapping.PrintModelMapper
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PrintModelService(
    private val dataService: PrintModelDataService,
    private val mapper: PrintModelMapper
) {

    suspend fun getPrintModelsPage(
        searchParams: PrintModelSearchParams,
        pageable: Pageable
    ): Page<PrintModel> {
        val dataPage = dataService.getPrintModels(searchParams, pageable)
        return PageImpl(mapper.dataToApi(dataPage.content), pageable, dataPage.totalElements)
    }

    suspend fun getPrintModelById(
        modelId: String
    ): PrintModel? {
        val model = dataService.getPrintModelById(modelId)
        return mapper.dataToApi(model)
    }
}