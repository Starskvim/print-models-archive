package com.starskvim.print.models.archive.utils

import com.starskvim.print.models.archive.utils.Constants.ModelCategory.FIGURE
import com.starskvim.print.models.archive.utils.Constants.ModelCategory.OTHER
import com.starskvim.print.models.archive.utils.Constants.ModelCategory.PACK
import com.starskvim.print.models.archive.utils.Constants.Regexp.BACKSLASH_REG
import com.starskvim.print.models.archive.utils.Constants.Regexp.CLEAR_NAME_REG
import com.starskvim.print.models.archive.utils.Constants.Regexp.EC_REG
import com.starskvim.print.models.archive.utils.Constants.Regexp.SLASH_REG
import com.starskvim.print.models.archive.utils.Constants.Regexp.SQUARE_BRACKETS_REG
import com.starskvim.print.models.archive.utils.Constants.Service.EC
import com.starskvim.print.models.archive.utils.Constants.Service.EMPTY
import com.starskvim.print.models.archive.utils.Constants.Service.HYPHEN
import com.starskvim.print.models.archive.utils.Constants.Service.PLUS
import com.starskvim.print.models.archive.utils.Constants.Service.SC
import org.springframework.util.StringUtils
import java.io.File

object CreateUtils {

    fun getPrintModelCategory(path: String): String {
        return when {
            path.contains(FIGURE) -> FIGURE
            path.contains(PACK) -> PACK
            else -> OTHER
        }
    }

    fun getAllPrintModelCategories(path: String): MutableList<String> {
        val categories = mutableListOf<String>()
        var splitString = path.split(BACKSLASH_REG)
        if (splitString.size == 1) {
            splitString = path.split(SLASH_REG)
        }
        for (word in splitString) {
            if (SQUARE_BRACKETS_REG.matches(word)) {
                val stringBuilder = StringBuilder()
                for (ch in word.toCharArray()) {
                    when (ch) {
                        SC -> continue
                        EC -> break
                        else -> stringBuilder.append(ch)
                    }
                }
                categories.add(stringBuilder.toString())
            }
        }
        return categories
    }

    fun getMyRateForModel(path: String): Int {
        var myRate = StringUtils.countOccurrencesOf(path, PLUS)
        myRate = if (myRate == 1) 0 else (myRate - 1).coerceAtLeast(0)
        return myRate
    }

    fun isHaveTrigger(path: String, triggers: Collection<String>): Boolean {
        for (trigger in triggers) {
            if (path.contains(trigger)) {
                return true
            }
        }
        return false
    }

    fun getSizeFileDouble(file: File): Double {
        val inputSize = file.length() / 1024.0 / 1024.0
        val scale = Math.pow(10.0, 3.0)
        return Math.round(inputSize * scale) / scale
    }

    fun getStorageName(modelName: String, fileName: String): String {
        return modelName + HYPHEN + fileName
    }

    fun clearModelName(name: String): String = name
        .replace(CLEAR_NAME_REG, EMPTY)
        .replaceFirstChar { it.uppercase() }

    fun isNotArtefact(name: String): Boolean {
        return name.contains(EC_REG).not()
    }
}
