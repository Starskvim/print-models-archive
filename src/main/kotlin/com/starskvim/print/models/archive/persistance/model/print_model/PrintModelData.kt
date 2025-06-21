package com.starskvim.print.models.archive.persistance.model.print_model

import com.starskvim.print.models.archive.persistance.model.Auditable
import com.starskvim.print.models.archive.persistance.model.print_model.meta.Meta
import com.starskvim.print.models.archive.utils.Constants.Document.PRINT_MODELS
import com.starskvim.print.models.archive.utils.Constants.TypeAlias.PRINT_MODEL_DATA
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@TypeAlias(PRINT_MODEL_DATA)
@Document(PRINT_MODELS)
data class PrintModelData(

    @Id
    var id: String?,
    var modelName: String,
    var folderName: String?,
    var path: String?,
    var category: String?,
    var rate: Int?,
    var nsfw: Boolean?,
    var categories: MutableList<String>?,
    var preview: String?, // storageName in Oth
    var zips: MutableList<PrintModelZipData>?,
    var oths: MutableList<PrintModelOthData>?,
    val addedAt: LocalDateTime,
    var meta: Meta?,
    override var createdAt: LocalDateTime, // todo time + index
    override var modifiedAt: LocalDateTime

) : Auditable(createdAt, modifiedAt) {

    fun hasPreview(): Boolean = preview != null

    fun hasImages(): Boolean = oths != null

    fun getLazyMeta(): Meta {
        if (meta != null) {
            return meta!!
        }
        meta = Meta(mutableListOf(), mutableSetOf())
        return meta!!
    }
}
