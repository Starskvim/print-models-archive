package com.starskvim.print.models.archive.domain

import com.starskvim.print.models.archive.mapping.PrintModelMapper
import com.starskvim.print.models.archive.persistance.PrintModelDataSearchService
import com.starskvim.print.models.archive.persistance.UserFavoritesDataService
import com.starskvim.print.models.archive.rest.model.ptint_model.PrintModel
import com.starskvim.print.models.archive.rest.model.request.PrintModelSearchParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PrintModelFavoritesService(
    private val searchDataService: PrintModelDataSearchService,
    private val favoritesDataService: UserFavoritesDataService,
    private val mapper: PrintModelMapper
) {

    suspend fun getPrintModelFavoritesPage(
        pageable: Pageable
    ): Page<PrintModel> {
        val favorites = favoritesDataService.getAdminFavorites()
        val modelNames = favorites
            ?.models
            ?.sortedByDescending { it.addedAt }
            ?.map { it.modelName }
            ?: return PageImpl(emptyList(), pageable, 0)
        val dataPage = searchDataService.getPrintModelsPage(
            PrintModelSearchParams(modelNames = modelNames),
            pageable
        )
        return PageImpl(mapper.dataToApi(dataPage.content), pageable, dataPage.totalElements)
    }
}
