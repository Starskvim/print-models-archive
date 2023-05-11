package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

data class PrintModelZip (

    val fileName: String?,
    val path: String?,
    val format: String?,
    val size: Double?,
    val ratio: Int?
)