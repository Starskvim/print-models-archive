package com.starskvim.print.models.archive.utils

import com.starskvim.print.models.archive.domain.model.OpExceptionResult
import mu.KLogger


object WrapUtils {

    inline fun <S, R> wrapException(
        source: S?,
        result: () -> R
    ): OpExceptionResult<S, R> {
        return try {
            OpExceptionResult(source, result())
        } catch (ex: Exception) {
            OpExceptionResult(source, ex)
        }
    }

    inline fun <R> wrapException(
        result: () -> R
    ): OpExceptionResult<Unit, R> {
        return try {
            OpExceptionResult(null, result())
        } catch (ex: Exception) {
            OpExceptionResult(null, ex)
        }
    }

    fun <S, R> OpExceptionResult<S, R>.logErrorIfFail(
        errorMessage: String,
        logger: KLogger
    ): OpExceptionResult<S, R> {
        if (isNoSuccess()) {
            logger.error(exception) { errorMessage }
        }
        return this
    }
}