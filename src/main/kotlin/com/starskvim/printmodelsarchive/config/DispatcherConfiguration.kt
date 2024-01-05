package com.starskvim.printmodelsarchive.config

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
class DispatcherConfiguration {

    @Bean
    fun dispatcher(): ExecutorCoroutineDispatcher {
        return Executors
            .newFixedThreadPool(6)
            .asCoroutineDispatcher()
    }
}