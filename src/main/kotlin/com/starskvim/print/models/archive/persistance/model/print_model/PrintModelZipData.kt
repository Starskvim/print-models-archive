package com.starskvim.print.models.archive.persistance.model.print_model

data class PrintModelZipData (

    var parentFileName: String,
    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var ratio: Int?

)