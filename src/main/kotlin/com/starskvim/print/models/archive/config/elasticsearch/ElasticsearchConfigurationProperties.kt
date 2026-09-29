package com.starskvim.print.models.archive.config.elasticsearch

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "elasticsearch")
data class ElasticsearchConfigurationProperties(

    val hostName: String,
    val port: Int

)
