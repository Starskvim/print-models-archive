package com.starskvim.printmodelsarchive.persistance.model

import com.starskvim.printmodelsarchive.utils.Constants.Document.PRINT_MODELS
import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.PRINT_MODEL_DATA
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@TypeAlias(PRINT_MODEL_DATA)
@Document(PRINT_MODELS)
data class PrintModelData(

    var preview: String?,
    var modelName: String?,
    var path: String?,
    var category: String?,
    var rate: Int?,
    var nsfw: Boolean?,
    var categories: MutableList<String>?,
    var zips: MutableList<PrintModelZipData>?,
    var oths: MutableList<PrintModelOthData>?,
    override var id: String?,
    override var createdAt: LocalDate?,
    override var modifiedAt: LocalDate?,
) : Auditable(id, createdAt, modifiedAt) {

    fun hasPreview(): Boolean = preview != null
}