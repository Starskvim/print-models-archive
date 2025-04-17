package com.starskvim.print.models.archive.config.ai

import com.starskvim.print.models.archive.config.ai.GeminiWebClientConfig.Companion.logger
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

object LogUtils {

    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            logger.info(">>> {}", clientRequest.method())
            // clientRequest.headers().forEach { name, values -> log.info("  {}: {}", name, values) }
            Mono.just(clientRequest)
        }
    }

    fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            logger.info("<<< Status: {}", clientResponse.statusCode())
            // clientResponse.headers().asHttpHeaders().forEach { name, values -> log.info("  {}: {}", name, values) }
            Mono.just(clientResponse)
        }
    }

}