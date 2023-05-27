package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

data class PrintModel(

    var preview: String?,
    var modelName: String?,
    var folderName: String?,
    var path: String?,
    var category: String?,
    var rate: Int?,
    var nsfw: Boolean?,
    var categories: List<String>?,
    var zips: List<PrintModelZip>?,
    var oths: List<PrintModelOth>?,
    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt)