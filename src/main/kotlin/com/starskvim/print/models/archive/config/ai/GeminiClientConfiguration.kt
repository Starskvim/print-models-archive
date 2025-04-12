package com.starskvim.print.models.archive.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.gemini")
class GeminiClientConfiguration (

    val baseUrl: String,
    val model: String,
    val processorName: String,
    val apikey: String,
    val batchSize: Int,
    val excludeTags: Set<String>

)