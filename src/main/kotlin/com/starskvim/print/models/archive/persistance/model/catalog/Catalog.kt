package com.starskvim.print.models.archive.persistance.model.catalog

import com.starskvim.print.models.archive.utils.Constants.TypeAlias.CATALOG
import org.springframework.data.annotation.TypeAlias

@TypeAlias(CATALOG)
data class Catalog(

    val catalog: MutableCollection<Category>

)