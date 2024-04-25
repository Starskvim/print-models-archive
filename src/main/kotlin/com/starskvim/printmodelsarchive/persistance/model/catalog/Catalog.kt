package com.starskvim.printmodelsarchive.persistance.model.catalog

import com.starskvim.printmodelsarchive.utils.Constants.TypeAlias.CATALOG
import org.springframework.data.annotation.TypeAlias

@TypeAlias(CATALOG)
data class Catalog(

    val catalog: MutableCollection<Category>

)