package com.starskvim.printmodelsarchive.config.s3

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

@ConfigurationProperties(prefix = "s3")
data class MinioConfig(

    val endpoint: String,
    val secretKey: String,
    val accessKey: String

) {

    @Bean
    fun minioClient(): MinioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
}