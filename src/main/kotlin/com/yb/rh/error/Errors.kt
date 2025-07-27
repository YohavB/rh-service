package com.yb.rh.error

open class RHException(
    errorMessage: String,
    open val errorType: ErrorType = ErrorType.UNKNOWN,
    throwable: Throwable? = null,
) : RuntimeException(errorMessage, throwable)