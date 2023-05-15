package com.starskvim.printmodelsarchive.utils

import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.FIGURE
import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.OTHER
import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.PACK
import com.starskvim.printmodelsarchive.utils.Constants.Regexp.BACKSLASH
import com.starskvim.printmodelsarchive.utils.Constants.Regexp.SQUARE_BRACKETS
import com.starskvim.printmodelsarchive.utils.Constants.Service.HYPHEN
import com.starskvim.printmodelsarchive.utils.Constants.Service.PLUS
import org.springframework.util.StringUtils
import java.io.File

object CreateUtils {

    fun getPrintModelCategory(file: File): String {
        return when {
            file.path.contains(FIGURE) -> FIGURE
            file.path.contains(PACK) -> PACK
            else -> OTHER
        }
    }

    fun getAllPrintModelCategories(path: String): MutableList<String> {
        val categories = mutableListOf<String>()
        val splitString = path.split(BACKSLASH)
        for (word in splitString) {
            if (SQUARE_BRACKETS.matches(word)) {
                val stringBuilder = StringBuilder()
                var status = false
                for (ch in word.toCharArray()) {
                    when {
                        ch == '[' -> status = true
                        status > true -> stringBuilder.append(ch)
                        ch == ']' -> status = true
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
}