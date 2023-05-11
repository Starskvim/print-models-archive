package com.starskvim.printmodelsarchive.persistance.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

abstract class Auditable(

    @Id
    open val id: String?,
    @Field
    open val createdAt: LocalDate?,
    @Field
    open val modifiedAt: LocalDate?
)
