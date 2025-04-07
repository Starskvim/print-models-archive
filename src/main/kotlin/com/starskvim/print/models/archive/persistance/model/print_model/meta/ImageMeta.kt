package com.starskvim.print.models.archive.persistance.model.print_model.meta

data class ImageMeta(
    var fileName: String,
    var processor: String,
    var tags: List<String>
) {
}