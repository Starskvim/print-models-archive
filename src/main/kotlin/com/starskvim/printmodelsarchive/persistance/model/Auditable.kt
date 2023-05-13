package com.starskvim.printmodelsarchive.persistance.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDate

abstract class Auditable(

    @Id
    open var id: String?,
    @Field(name = "created_at")
    open var createdAt: LocalDate?,
    @Field(name = "modified_at")
    open var modifiedAt: LocalDate?
)
