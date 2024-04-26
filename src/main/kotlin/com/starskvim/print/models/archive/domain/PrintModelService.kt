package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.mapping.PrintModelMapper
import com.starskvim.print.models.archive.persistance.PrintModelDataService
import com.starskvim.print.models.archive.rest.model.PrintModel
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

//import ru.starskvim.inrastructure.webflux.advice.exception.NotFoundException

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
    ): PrintModel {
        val model = dataService.getPrintModelById(modelId)
        return mapper.dataToApi(model)
//            ?: throw NotFoundException(MODEL_NOT_FOUND)
            ?: throw RuntimeException()
    }
}
