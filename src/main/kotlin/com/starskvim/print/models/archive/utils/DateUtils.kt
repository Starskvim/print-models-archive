package com.starskvim.print.models.archive.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object DateUtils {

    fun dateTimeFromLong(
        value: Long
    ): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(value),
            ZoneId.systemDefault()
        )
    }
}