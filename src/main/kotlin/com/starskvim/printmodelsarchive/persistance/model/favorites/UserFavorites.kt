package com.starskvim.printmodelsarchive.persistance.model.favorites

import com.starskvim.printmodelsarchive.persistance.model.Auditable
import com.starskvim.printmodelsarchive.utils.Constants.Document.USER_FAVORITES
import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.USER_FAVORITES_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@TypeAlias(USER_FAVORITES_DATA)
@Document(USER_FAVORITES)
class UserFavorites(

    @Id
    val id: String,
    val models: MutableList<PrintModelFavorite>, // sort
    override var createdAt: LocalDateTime,
    override var modifiedAt: LocalDateTime

) : Auditable(createdAt, modifiedAt) {

    fun addModel(modelName: String) {
        models.add(
            PrintModelFavorite(
                modelName,
                LocalDateTime.now()
            )
        )
    }

    fun removeModel(modelName: String) {
        models.removeIf { it.modelName == modelName }
    }

    fun getModelNames(): List<String> = models.map { it.modelName }
}