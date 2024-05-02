package com.starskvim.print.models.archive.rest.model.ptint_model

import java.time.LocalDateTime

data class PrintModelCard(

    val id: String,
    var preview: String?,
    val modelName: String?,
    val rate: Int?,
    val nsfw: Boolean?,
    val addedAt: LocalDateTime

)