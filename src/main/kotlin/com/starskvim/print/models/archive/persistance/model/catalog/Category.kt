package com.starskvim.print.models.archive.persistance.model.catalog

data class Category(

    val name: String,
    val children: MutableList<Category>,
    var size: Int,
    var level: Int

)