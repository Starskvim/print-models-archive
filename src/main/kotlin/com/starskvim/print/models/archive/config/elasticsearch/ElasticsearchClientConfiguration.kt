package com.starskvim.print.models.archive.config.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class ElasticsearchClientConfiguration {

    @Bean
    fun elasticsearchClient(
        restClient: RestClient,
        mapper: JacksonJsonpMapper
    ): ElasticsearchClient {
        val transport: ElasticsearchTransport = RestClientTransport(
            restClient, mapper
        )
        return ElasticsearchClient(transport)
    }

    @Bean
    @Profile("local")
    fun elasticsearchLocalRestClient(
        properties: ElasticsearchConfigurationProperties
    ): RestClient {
        return RestClient.builder(
            HttpHost(properties.hostName, properties.port)
        ).build()
    }

}