package com.starskvim.print.models.archive.rest.model.ptint_model

import com.starskvim.print.models.archive.rest.model.Auditable
import java.time.LocalDate

data class PrintModel(

    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
    var preview: String?,
    var modelName: String?,
    var folderName: String?,
    var path: String?,
    var category: String?,
    var rate: Int?,
    var nsfw: Boolean?,
    var categories: List<String>?,
    var zips: List<PrintModelZip>?,
    var oths: List<PrintModelOth>?

) : Auditable(id, createdAt, modifiedAt)