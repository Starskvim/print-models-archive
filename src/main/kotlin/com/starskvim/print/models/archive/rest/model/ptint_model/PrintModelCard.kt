package com.starskvim.print.models.archive.rest.model.ptint_model

import java.time.LocalDateTime

data class PrintModelCard(

    val id: String,
    val modelName: String?,
    var preview: String?,
    var category: String?,
    var categories: MutableList<String>?,
    var images: List<String>?,
    val rate: Int?,
    val nsfw: Boolean?,
    val addedAt: LocalDateTime

)