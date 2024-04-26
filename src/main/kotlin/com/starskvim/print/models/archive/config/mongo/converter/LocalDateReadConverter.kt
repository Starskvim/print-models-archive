package com.starskvim.print.models.archive.config.mongo.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

@ReadingConverter
class LocalDateReadConverter : Converter<Date, LocalDate> {
    override fun convert(source: Date): LocalDate? = LocalDate.ofInstant(source.toInstant(), ZoneOffset.UTC)
}