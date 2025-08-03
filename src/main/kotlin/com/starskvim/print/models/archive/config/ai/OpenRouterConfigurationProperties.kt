package com.starskvim.print.models.archive.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openrouter")
class OpenRouterConfigurationProperties(
    var baseUrl: String = "https://openrouter.ai/api/v1",
    var model: String = "google/gemini-2.5-flash"
) {
}