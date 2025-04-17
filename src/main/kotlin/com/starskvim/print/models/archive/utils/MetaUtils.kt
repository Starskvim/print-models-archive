package com.starskvim.print.models.archive.utils

object MetaUtils {

    fun String?.splitTags(): List<String> {
        return this?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

}