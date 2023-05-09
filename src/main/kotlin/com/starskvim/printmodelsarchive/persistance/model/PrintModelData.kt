package com.starskvim.printmodelsarchive.persistance.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document("123")
data class PrintModelData(

    val modelName: String

) : Auditable()