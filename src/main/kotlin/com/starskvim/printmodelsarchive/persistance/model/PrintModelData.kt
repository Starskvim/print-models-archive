package com.starskvim.printmodelsarchive.persistance.model

import com.starskvim.printmodelsarchive.utils.Constants.PRINT_MODELS
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(PRINT_MODELS)
data class PrintModelData(

    val modelName: String?,
    val path: String?,
    val rate: String?,
    val nsfw: Boolean?,
    val zips: List<PrintModelZipData>?,
    val oths: List<PrintModelOthData>?,
    override val id: String?,
    override val createdAt: LocalDate?,
    override val modifiedAt: LocalDate?,
    ) : Auditable(id, createdAt, modifiedAt)