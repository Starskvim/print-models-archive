package com.starskvim.print.models.archive.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "archive-properties")
class ArchiveConfiguration(

    val version: String,
    val saveBatch: Int,
    val coroutineBatch: Int

)