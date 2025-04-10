package com.starskvim.print.models.archive.domain.model

import com.starskvim.print.models.archive.persistance.model.print_model.meta.Meta

class PrintModelContext(
    var version: Int? = 0,
    var description: String?,
    var meta: Meta
) {
}