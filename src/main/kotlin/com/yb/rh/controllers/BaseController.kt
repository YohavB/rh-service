package com.yb.rh.controllers

import com.github.michaelbull.result.*
import com.yb.rh.error.RHException
import com.yb.rh.utils.RHResponse
import com.yb.rh.utils.SuccessResponse
import com.yb.rh.utils.Utils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity

open class BaseController {
    var logger = KotlinLogging.logger {}

    fun <T> handleServiceResult(
        result: Result<T, RHException>,
        successMessage: String,
        failureMessage: String
    ): ResponseEntity<out RHResponse> {
        return result
            .onSuccess { logger.info { successMessage } }
            .onFailure { logger.warn(it) { "$failureMessage: ${it.message}" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }
}