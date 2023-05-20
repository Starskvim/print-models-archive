package com.starskvim.printmodelsarchive.persistance.model

import com.starskvim.printmodelsarchive.utils.Constants.Document.CATEGORIES_INFO
import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.CATEGORIES_INFO_DATA
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@TypeAlias(CATEGORIES_INFO_DATA)
@Document(CATEGORIES_INFO)
data class CategoriesInfoData(

    var categories: MutableList<String>?,
    var countInfo: Map<String, Int>?,
    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt)
