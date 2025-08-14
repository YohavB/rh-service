package com.yb.rh.error

data class ApiError(
    val code: ErrorType,
    val message: String,
    val status: Int,
)

