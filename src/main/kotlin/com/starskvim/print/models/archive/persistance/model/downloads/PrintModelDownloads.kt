package com.starskvim.print.models.archive.persistance.model.downloads

import com.starskvim.print.models.archive.utils.Constants.TypeAlias.PRINT_MODEL_DOWNLOADS
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Field

@TypeAlias(PRINT_MODEL_DOWNLOADS)
data class PrintModelDownloads(

    @Id
    @Field
    var id: String?,
    val ids: MutableList<String>

)