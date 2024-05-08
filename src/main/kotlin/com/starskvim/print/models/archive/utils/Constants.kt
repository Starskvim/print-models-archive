package com.starskvim.print.models.archive.utils

object Constants {

    object Url {
        const val API_MODELS = "/api/models"
        const val MODELS = "/models"
        const val CREATE_ARCHIVE = "/create-archive"
        const val UPDATE_ARCHIVE = "/update-archive"
        const val CLEAR_ARCHIVE = "/clear-archive"
        const val CHECK_FOLDERS = "/check-folders"
        const val GET_PROGRESS_TASK = "/progress-task"
    }

    object Task {
        const val INITIALIZE_ARCHIVE_TASK = "Initialize archive task."
        const val UPDATE_ARCHIVE_TASK = "Update archive task."
    }

    object Service {
        const val PLUS = "+"
        const val UNDERLINE = "_"
        const val HYPHEN = "-"
        const val EMPTY = ""
        const val ZERO_INT = 0
        const val ONE_INT = 1
        const val SC = '['
        const val EC = ']'
    }

    object Document {
        const val CATEGORIES_INFO = "categories_info"
        const val PRINT_MODELS = "print_models"
        const val USER_FAVORITES = "user_favorites"
    }

    object Data {
        const val ADMIN_FAVORITES_ID = "admin_favorites_id"
    }

    object TypeAlias {
        const val PRINT_MODEL_DATA = "PrintModelData"
        const val CATEGORIES_INFO_DATA = "CategoriesInfoData"
        const val USER_FAVORITES_DATA = "UserFavoritesData"
        const val PRINT_MODEL_FAVORITES = "PrintModelFavorites"
        const val CATALOG = "Catalog"
    }

    object Bucket {
        const val PRINT_MODEL_IMAGE = "print-model-image"
    }

    object ModelCategory {
        const val FIGURE = "Figure"
        const val PACK = "Pack"
        const val OTHER = "Other"
    }

    object Triggers {
        val ZIP_FORMATS = setOf(
            "zip", "7z", "7zip", "rar", "ZIP", "RAR", "7ZIP"
        )
        val IMAGE_FORMATS_TRIGGERS = setOf(
            "jpg", "jpeg", "png", "gif", "webp", "JPG", "JPEG", "PNG", "GIF", "WEBP"
        )
        val NSFW_TRIGGERS = setOf(
            "nsfw", "NSFW", "Nsfw", "Nude", "NUDE", "nude", "18+", "Adult", "adult", "ADULT",
            "Digital Dark Pin-Ups", "Dungeon Pin-ups", "Pink Studio", "Rushzilla", "Rubim", "Texelion"
        )
    }

    object Regexp {
        val BACKSLASH_REG = "\\\\".toRegex()
        val SLASH_REG = "/".toRegex()
        val SQUARE_BRACKETS_REG = """.*\[.*\].*""".toRegex()
        val CLEAR_NAME_REG = "[+|_]".toRegex()
        val EC_REG = "]".toRegex()
    }

    object Fields {
        const val ID = "_id"
        const val MODEL_NAME = "modelName"
        const val FOLDER_NAME = "folderName"
        const val CATEGORIES = "categories"
        const val NSFW = "nsfw"
        const val ZIPS = "zips"
        const val OTHS = "oths"
        const val RATE = "rate"
    }
}