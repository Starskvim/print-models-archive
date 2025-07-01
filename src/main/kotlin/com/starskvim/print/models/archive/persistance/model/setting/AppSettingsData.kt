package com.starskvim.print.models.archive.persistance.model.setting

import com.starskvim.print.models.archive.utils.Constants
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias(Constants.TypeAlias.APP_SETTINGS)
@Document(Constants.Document.APP_SETTINGS)
class AppSettingsData(

    @Id
    var id: String,
    var imageAiMetaJob: Boolean // def false

) {
}