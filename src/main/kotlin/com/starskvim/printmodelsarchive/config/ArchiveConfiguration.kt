package com.starskvim.printmodelsarchive.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "archive-properties")
class ArchiveConfiguration(

    val saveBatch: Int,
    val coroutineBatch: Int

)