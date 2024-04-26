package com.starskvim.print.models.archive.rest.model.request

data class PrintModelSearchParams(

    val modelName: String?,
    val category: String?,
    val rate: Int?,
    val modelNames: List<String>?

) {
    constructor(
        modelName: String?,
        category: String?,
        rate: Int?
    ) : this(
        modelName = modelName,
        category = category,
        rate = rate,
        modelNames = null
    )

    constructor(
        modelNames: List<String>
    ) : this(
        modelName = null,
        category = null,
        rate = null,
        modelNames = modelNames
    )
}