package com.starskvim.print.models.archive.domain.meta.gemini

class GeminiLimitRequestException (
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)