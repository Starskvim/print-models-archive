package com.starskvim.print.models.archive.domain.print_model

import com.starskvim.print.models.archive.persistance.UserFavoritesDataService
import com.starskvim.print.models.archive.persistance.model.favorites.UserFavorites
import com.starskvim.print.models.archive.utils.Constants.Data.ADMIN_FAVORITES_ID
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
class UserFavoritesService(
    private val dataService: UserFavoritesDataService
) {

    suspend fun add(modelName: String): UserFavorites {
        return dataService.findById(ADMIN_FAVORITES_ID) ?: initFavorites()
            .apply { addModel(modelName) }
            .let { dataService.save(it) }
    }

    suspend fun remove(modelName: String): UserFavorites {
        return dataService.findById(ADMIN_FAVORITES_ID) ?: initFavorites()
            .apply { removeModel(modelName) }
            .let { dataService.save(it) }
    }

    private suspend fun initFavorites(): UserFavorites {
        return UserFavorites(ADMIN_FAVORITES_ID, mutableListOf(), now(), now())
    }
}
