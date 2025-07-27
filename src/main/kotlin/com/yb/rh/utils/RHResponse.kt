package com.yb.rh.utils

import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.yb.rh.error.ErrorType.*
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity

open class RHResponse

private val logger = KotlinLogging.logger {}

class ErrorResponse(val cause: String, val errorCode: Int? = null) : RHResponse() {
    companion object Factory {
        fun withErrorMessage(message: String?) = ErrorResponse(message ?: "no error message provided")
    }
}

class SuccessResponse<T>(@JsonUnwrapped val entity: T) : RHResponse()

object Utils {
    fun mapRHErrorToResponse(rHException: RHException): ResponseEntity<ErrorResponse> {
        return when (rHException.errorType) {
            AUTHENTICATION -> ResponseEntity.status(UNAUTHORIZED)
                .body(ErrorResponse.withErrorMessage(rHException.message))
            INVALID_JWT, CORRUPTED_DATA, INVALID_INPUT, DB_ACCESS ->
                ResponseEntity.status(UNPROCESSABLE_ENTITY)
                    .body(ErrorResponse.withErrorMessage(rHException.message))
            RESOURCE_ALREADY_EXISTS -> ResponseEntity.status(CONFLICT)
                .body(ErrorResponse.withErrorMessage(rHException.message))
            RESOURCE_NOT_EXISTS, ENTITY_NOT_FOUND -> ResponseEntity.status(NOT_FOUND)
                .body(ErrorResponse.withErrorMessage(rHException.message))
            BAD_CREDENTIAL, CAR_HAS_NO_OWNER -> ResponseEntity.status(FORBIDDEN)
                .body(ErrorResponse.withErrorMessage(rHException.message))
            HTTP_CALL -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.withErrorMessage(rHException.message))
            UNKNOWN -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.withErrorMessage(rHException.message))
        }.also { logger.warn { it.body?.cause } }
    }
}