package com.starskvim.print.models.archive.persistance.model.catalog

import com.starskvim.print.models.archive.persistance.model.Auditable
import com.starskvim.print.models.archive.utils.Constants.Document.CATEGORIES_INFO
import com.starskvim.print.models.archive.utils.Constants.TypeAlias.CATEGORIES_INFO_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime

@TypeAlias(CATEGORIES_INFO_DATA)
@Document(CATEGORIES_INFO)
data class CategoriesInfoData(

    @Id
    @Field
    var id: String?,
    var categories: MutableList<String>?,
    var categoriesCatalog: Catalog,
    override var createdAt: LocalDateTime,
    override var modifiedAt: LocalDateTime

) : Auditable(createdAt, modifiedAt)
