package com.starskvim.print.models.archive.persistance.model.print_model.meta

data class Meta(
    var images: List<ImageMeta>,
    var processors: List<String>
) {
}