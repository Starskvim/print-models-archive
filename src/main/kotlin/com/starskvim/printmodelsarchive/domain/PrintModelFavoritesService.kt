package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.mapping.PrintModelMapper
import com.starskvim.printmodelsarchive.persistance.PrintModelDataService
import com.starskvim.printmodelsarchive.persistance.UserFavoritesDataService
import com.starskvim.printmodelsarchive.rest.model.PrintModel
import com.starskvim.printmodelsarchive.rest.model.request.PrintModelSearchParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PrintModelFavoritesService(
    private val dataService: PrintModelDataService,
    private val favoritesDataService: UserFavoritesDataService,
    private val mapper: PrintModelMapper
) {

    suspend fun getPrintModelFavoritesPage(
        pageable: Pageable
    ): Page<PrintModel> {
        val favorites = favoritesDataService.getAdminFavorites()
        val modelNames = favorites?.getModelNames() ?: return Page.empty()
        val dataPage = dataService.getPrintModels(
            PrintModelSearchParams(modelNames = modelNames),
            pageable
        )
        return PageImpl(mapper.dataToApi(dataPage.content), pageable, dataPage.totalElements)
    }
}