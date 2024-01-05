package com.starskvim.printmodelsarchive.persistance.model

data class PrintModelZipData (

    var parentFileName: String,
    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var ratio: Int?

)