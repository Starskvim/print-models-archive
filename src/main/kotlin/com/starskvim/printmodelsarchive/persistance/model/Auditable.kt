package com.starskvim.printmodelsarchive.persistance.model

import org.springframework.data.annotation.Id
import java.time.LocalDate

abstract class Auditable(
    @Id
    val id: String?=null,
    val createdAt: LocalDate?=null,
    val modifiedAt: LocalDate?=null
)
