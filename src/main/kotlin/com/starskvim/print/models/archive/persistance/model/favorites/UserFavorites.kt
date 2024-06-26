package com.starskvim.print.models.archive.persistance.model.favorites

import com.starskvim.print.models.archive.persistance.model.Auditable
import com.starskvim.print.models.archive.utils.Constants.Document.USER_FAVORITES
import com.starskvim.print.models.archive.utils.Constants.TypeAlias.USER_FAVORITES_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@TypeAlias(USER_FAVORITES_DATA)
@Document(USER_FAVORITES)
class UserFavorites(

    @Id
    @Field
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