package com.starskvim.print.models.archive.domain.model

data class OpExceptionResult<S, R>(

    val source: S?,
    val result: R?,
    val exception: Exception?

) {
    constructor(source: S?, exception: Exception) : this(source, null, exception)

    constructor(source: S?, result: R) : this(source, result, null)

    fun isSuccess(): Boolean = exception == null

    fun isNoSuccess(): Boolean = exception != null

    fun isNullPointerException(): Boolean = exception is NullPointerException

    suspend fun onException(block: suspend () -> Unit): OpExceptionResult<S, R> {
        if (exception != null) {
            block()
        }
        return this
    }

    suspend fun onSuccess(block: suspend () -> Unit): OpExceptionResult<S, R> {
        if (exception == null) {
            block()
        }
        return this
    }
}