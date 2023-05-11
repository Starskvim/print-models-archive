package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

data class PrintModel(

    val modelName: String?,
    val path: String?,
    val rate: String?,
    val nsfw: Boolean?,
    val zips: List<PrintModelZip>?,
    val oths: List<PrintModelOth>?,
    override val id: String?,
    override val createdAt: LocalDate?,
    override val modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt)