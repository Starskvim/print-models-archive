package com.starskvim.printmodelsarchive.persistance.model

import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@TypeAlias("CategoriesInfoData")
@Document("categories_info_data")
data class CategoriesInfoData(

    var categories: MutableList<String>?,
    var countInfo: Map<String, Int>?,
    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt)
