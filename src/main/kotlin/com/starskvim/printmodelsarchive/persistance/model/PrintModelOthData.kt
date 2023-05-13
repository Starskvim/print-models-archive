package com.starskvim.printmodelsarchive.persistance.model

import java.time.LocalDate

data class PrintModelOthData(

    var fileName: String? = null,
    var path: String? = null,
    var format: String? = null,
    var size: Double? = null,
    var preview: String? = null
)