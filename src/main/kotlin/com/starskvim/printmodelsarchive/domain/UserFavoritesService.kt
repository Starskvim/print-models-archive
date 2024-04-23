package com.starskvim.printmodelsarchive.domain

import com.starskvim.printmodelsarchive.persistance.UserFavoritesDataService
import com.starskvim.printmodelsarchive.persistance.model.favorites.UserFavorites
import com.starskvim.printmodelsarchive.utils.Constants.Data.ADMIN_FAVORITES_ID
import org.springframework.stereotype.Service

@Service
class UserFavoritesService(
    private val dataService: UserFavoritesDataService
) {

    suspend fun add(modelName: String): UserFavorites {
        return dataService.findById(ADMIN_FAVORITES_ID) ?: initFavorites()
            .apply { addModel(modelName) }
            .let { dataService.save(it) }
    }

    private suspend fun initFavorites(): UserFavorites {
        return UserFavorites(ADMIN_FAVORITES_ID, mutableListOf())
    }
}