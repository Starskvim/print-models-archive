package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

abstract class Auditable(

    open val id: String?,
    open val createdAt: LocalDate?,
    open val modifiedAt: LocalDate?
)