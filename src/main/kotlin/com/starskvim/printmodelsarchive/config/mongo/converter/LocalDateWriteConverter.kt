package com.starskvim.printmodelsarchive.config.mongo.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

@WritingConverter
class LocalDateWriteConverter : Converter<LocalDate, Date> {
    override fun convert(source: LocalDate): Date? = Date.from(source.atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
}