package com.starskvim.print.models.archive.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openrouter")
class OpenRouterConfiguration(
    var apiKey: String = "",
    var baseUrl: String = "https://openrouter.ai/api/v1",
    var model: String = "openai/gpt-4.1-nano"
) {
}