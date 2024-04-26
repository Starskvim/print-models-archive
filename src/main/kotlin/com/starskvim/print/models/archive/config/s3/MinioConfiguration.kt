package com.starskvim.print.models.archive.config.s3

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

@ConfigurationProperties(prefix = "minio")
data class MinioConfiguration(

    val endpoint: String,
    val external: String,
    val secretKey: String,
    val accessKey: String

) {

    @Bean
    fun minioClient(): MinioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
}
