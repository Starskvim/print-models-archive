package com.starskvim.printmodelsarchive.persistance.model

import java.time.LocalDate

data class PrintModelOthData(

    val fileName: String? = null,
    val path: String? = null,
    val format: String? = null,
    val size: Double? = null,
    val preview: String? = null
)