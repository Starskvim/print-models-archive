package com.starskvim.print.models.archive.persistance.model.print_model.meta

data class Meta(
    var images: MutableList<ImageMeta> = mutableListOf(),
    var processors: MutableSet<String> = mutableSetOf()
) {

}