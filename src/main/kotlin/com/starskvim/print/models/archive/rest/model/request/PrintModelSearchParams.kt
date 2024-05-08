package com.starskvim.print.models.archive.rest.model.request

data class PrintModelSearchParams(

    val modelName: String?,
    val category: String?,
    val rate: Int?,
    val modelNames: List<String>?,
    val nsfwOnly: Boolean?

) {
    constructor(
        modelName: String? = null,
        category: String? = null,
        rate: Int? = null,
        nsfwOnly: Boolean? = null
    ) : this(
        modelName = modelName,
        category = category,
        rate = rate,
        nsfwOnly = nsfwOnly,
        modelNames = null
    )

    constructor(
        modelNames: List<String>
    ) : this(
        modelName = null,
        category = null,
        rate = null,
        nsfwOnly = null,
        modelNames = modelNames
    )
}