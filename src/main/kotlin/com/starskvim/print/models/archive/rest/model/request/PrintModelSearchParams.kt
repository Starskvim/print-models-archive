package com.starskvim.print.models.archive.rest.model.request

data class PrintModelSearchParams(

    val modelName: String? = null,
    val category: String? = null,
    val rate: Int? = null,
    val modelNames: List<String>? = null,
    val nsfwOnly: Boolean? = null

)