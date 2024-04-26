package com.starskvim.print.models.archive.persistance.model.favorites

import com.starskvim.print.models.archive.utils.Constants.TypeAlias.PRINT_MODEL_FAVORITES
import org.springframework.data.annotation.TypeAlias
import java.time.LocalDateTime

@TypeAlias(PRINT_MODEL_FAVORITES)
class PrintModelFavorite(

    val modelName: String,
    val addedAt: LocalDateTime

)