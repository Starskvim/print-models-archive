package com.starskvim.printmodelsarchive.utils

import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.FIGURE
import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.OTHER
import com.starskvim.printmodelsarchive.utils.Constants.ModelCategory.PACK
import org.springframework.util.StringUtils
import java.io.File

object CreateUtils {

    fun detectPrintModelCategory(file: File): String {
        return if (file.path.contains(FIGURE)) {
            FIGURE
        } else if (file.path.contains(PACK)) {
            PACK
        } else {
            OTHER
        }
    }

    fun detectMyRateForModel(path: String): Int {
        var myRate = StringUtils.countOccurrencesOf(path, "+")
        myRate = if (myRate == 1) 0 else (myRate - 1).coerceAtLeast(0)
        return myRate
    }

    fun detectTrigger(path: String, triggers: Collection<String>): Boolean {
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
}