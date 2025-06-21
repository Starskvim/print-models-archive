package com.starskvim.print.models.archive.persistance.model.print_model.meta

data class Meta(
    var images: MutableList<ImageMeta>,
    var processors: MutableSet<String>
) {



}