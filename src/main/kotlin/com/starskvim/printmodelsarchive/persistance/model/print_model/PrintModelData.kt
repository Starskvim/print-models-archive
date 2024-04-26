package com.starskvim.printmodelsarchive.persistance.model.print_model

import com.starskvim.printmodelsarchive.persistance.model.Auditable
import com.starskvim.printmodelsarchive.utils.Constants.Document.PRINT_MODELS
import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.PRINT_MODEL_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@TypeAlias(PRINT_MODEL_DATA)
@Document(PRINT_MODELS)
data class PrintModelData(

    @Id
    var id: String?,
    var modelName: String?,
    var folderName: String?,
    var path: String?,
    var category: String?,
    var rate: Int?,
    var nsfw: Boolean?,
    var categories: MutableList<String>?,
    var preview: String?,
    var zips: MutableList<PrintModelZipData>,
    var oths: MutableList<PrintModelOthData>,
    val addedAt: LocalDateTime,
    override var createdAt: LocalDateTime, // todo time + index
    override var modifiedAt: LocalDateTime

) : Auditable(createdAt, modifiedAt) {

    fun hasPreview(): Boolean = preview != null
}
