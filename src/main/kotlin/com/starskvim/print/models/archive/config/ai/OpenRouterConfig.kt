package com.starskvim.print.models.archive.config.ai

import com.starskvim.print.models.archive.config.ai.LogUtils.logRequest
import com.starskvim.print.models.archive.config.ai.LogUtils.logResponse
import com.starskvim.print.models.archive.domain.setting.AppSettingsService
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

@Configuration
class OpenRouterConfig(
    private val props: OpenRouterConfiguration
) {

    @Bean
    fun openRouterWebClient(
        webClientBuilder: WebClient.Builder,
        configService: AppSettingsService
    ): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(java.time.Duration.ofSeconds(60))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(60, TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(60, TimeUnit.SECONDS))
            }

        val apiKey = runBlocking {
            configService.getAppSettings().openRouterApiKey
        }

        return webClientBuilder
            .baseUrl(props.baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $apiKey")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(logRequest())
            .filter(logResponse())
            .build()
    }
}