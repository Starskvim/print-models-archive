package com.starskvim.printmodelsarchive.config.s3

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MinioConfig(

    @Value("\${s3.endpoint}")
    private val endpoint: String,
    @Value("\${s3.secretKey}")
    private val secretKey: String,
    @Value("\${s3.accessKey}")
    private val accessKey: String

) {

    @Bean
    fun minioClient(): MinioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()
}