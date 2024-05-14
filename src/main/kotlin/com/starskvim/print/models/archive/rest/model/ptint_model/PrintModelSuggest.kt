package com.starskvim.print.models.archive.rest.model.ptint_model

data class PrintModelSuggest(

    val id: String,
    val modelName: String?,
    var preview: String?,
    val category: String?,
    var mainCategory: String?,
    val rate: Int?,
    val nsfw: Boolean?

)