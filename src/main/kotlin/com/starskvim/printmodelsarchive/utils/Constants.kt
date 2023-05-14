package com.starskvim.printmodelsarchive.utils

object Constants {

    const val PRINT_MODELS = "print_models"

    const val API_MODELS = "/api/models"
    const val MODELS = "/models"
    const val CREATE_ARCHIVE = "/create-archive"

    object TypeAlias {
        const val PRINT_MODEL_DATA = "PrintModelData"
    }

    object Bucket {
        const val PRINT_MODEL_IMAGE = "print-model-image"
    }

    object ModelCategory {
        const val FIGURE = "[Figure]"
        const val PACK = "[Pack]"
        const val OTHER = "[Other]"
    }

    object Triggers {

        val ZIP_FORMATS = setOf(
            "zip", "7z", "7zip", "rar", "ZIP", "RAR", "7ZIP"
        )

        val IMAGE_FORMATS_TRIGGERS = setOf(
            "jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF"
        )

        val NSFW_TRIGGERS = setOf(
            "nsfw", "NSFW", "Nsfw", "Nude", "NUDE", "nude", "18+", "Adult", "adult", "ADULT",
            "Digital Dark Pin-Ups", "Dungeon Pin-ups", "Pink Studio", "Rushzilla", "Rubim", "Texelion"
        )
    }
}