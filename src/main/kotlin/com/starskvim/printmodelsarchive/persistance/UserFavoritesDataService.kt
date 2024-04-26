package com.starskvim.printmodelsarchive.persistance

import com.starskvim.printmodelsarchive.persistance.model.favorites.UserFavorites
import com.starskvim.printmodelsarchive.utils.Constants.Data.ADMIN_FAVORITES_ID
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.stereotype.Service

@Service
class UserFavoritesDataService(
    private val template: ReactiveMongoTemplate
) {

    suspend fun save(favorites: UserFavorites): UserFavorites = template.save(favorites)
        .awaitSingle()

    suspend fun getAdminFavorites() = findById(ADMIN_FAVORITES_ID)

    suspend fun findById(id: String): UserFavorites? {
        return template.findById(ObjectId(id), UserFavorites::class.java)
            .awaitFirstOrNull()
    }
}