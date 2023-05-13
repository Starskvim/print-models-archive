package com.starskvim.printmodelsarchive.persistance.model

import java.time.LocalDate

data class PrintModelZipData (

    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var ratio: Int?
)