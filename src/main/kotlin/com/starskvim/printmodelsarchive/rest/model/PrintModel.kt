package com.starskvim.printmodelsarchive.rest.model

import java.time.LocalDate

data class PrintModel(

    var modelName: String?,
    var path: String?,
    var rate: String?,
    var nsfw: Boolean?,
    var zips: List<PrintModelZip>?,
    var oths: List<PrintModelOth>?,
    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt)