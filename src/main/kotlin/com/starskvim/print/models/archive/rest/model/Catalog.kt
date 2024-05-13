package com.starskvim.print.models.archive.rest.model

import com.starskvim.print.models.archive.persistance.model.catalog.Category

data class Catalog(

    val catalog: List<Category>,
    val categories: List<Category>

)