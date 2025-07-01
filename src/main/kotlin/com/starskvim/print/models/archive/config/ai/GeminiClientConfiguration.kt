package com.starskvim.print.models.archive.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.gemini")
class GeminiClientConfiguration (

    val baseUrl: String,
    val model: String,
    val processorName: String,
    val models: List<AiModel>,
    val apikey: String,
    val batchSize: Int,
    val excludeTags: Set<String>

) {

    fun getFirstAvailableModel() : AiModel? {
        return models.find { it.canRequest() }
    }

    fun getModelStats() : String {
        val s = StringBuilder()
        models.forEach {
            s.append(" - Model: [${it.model}] [${it.limit}] [${it.currentReqCount.get()}]")
        }
        return s
    }

}