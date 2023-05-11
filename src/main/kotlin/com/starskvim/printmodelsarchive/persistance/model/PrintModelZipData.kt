package com.starskvim.printmodelsarchive.persistance.model

import java.time.LocalDate

data class PrintModelZipData (

    val fileName: String?,
    val path: String?,
    val format: String?,
    val size: Double?,
    val ratio: Int?
)