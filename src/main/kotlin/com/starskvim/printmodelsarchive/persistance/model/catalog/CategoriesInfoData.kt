package com.starskvim.printmodelsarchive.persistance.model.catalog

import com.starskvim.printmodelsarchive.persistance.model.Auditable
import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.CATEGORIES_INFO_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

@TypeAlias(CATEGORIES_INFO_DATA)
@Document(CATEGORIES_INFO)
data class CategoriesInfoData(

    @Id
    @Field
    var id: String?,
    var categories: MutableList<String>?,
    var countInfo: Map<String, Int>?,
    var catalog: Catalog,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?

) : Auditable(createdAt, modifiedAt)
