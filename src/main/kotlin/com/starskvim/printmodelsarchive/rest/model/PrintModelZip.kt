package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

data class PrintModelZip (

    var fileName: String?,
    var path: String?,
    var format: String?,
    var size: Double?,
    var ratio: Int?
)