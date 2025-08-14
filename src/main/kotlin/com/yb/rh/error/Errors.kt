package com.yb.rh.error

open class RHException(
    val errorMessage: String,
    val errorType: ErrorType = ErrorType.UNKNOWN,
    val throwable: Throwable? = null,
) : RuntimeException(errorMessage, throwable)