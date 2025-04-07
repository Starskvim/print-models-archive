package com.starskvim.print.models.archive.domain.meta

import org.springframework.http.HttpStatusCode

class GeminiApiException(
    message: String,
    cause: Throwable? = null,
    val statusCode: HttpStatusCode? = null
) : RuntimeException(message, cause)