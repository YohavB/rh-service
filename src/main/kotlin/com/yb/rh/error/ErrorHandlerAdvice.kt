package com.yb.rh.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

@ControllerAdvice
class ErrorHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseBody
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val status = ErrorMapper.toStatus(ErrorType.INVALID_INPUT)
        val errorMessage = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val body = ApiError(
            code = ErrorType.INVALID_INPUT,
            message = "Invalid input: $errorMessage",
            status = status.value()
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    @ResponseBody
    fun handleMissingRequestHeader(ex: MissingRequestHeaderException): ResponseEntity<ApiError> {
        val status = ErrorMapper.toStatus(ErrorType.INVALID_INPUT)
        val body = ApiError(
            code = ErrorType.INVALID_INPUT,
            message = "parameter `${ex.headerName}` is missing from request header",
            status = status.value()
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(RHException::class)
    @ResponseBody
    fun handleRHException(ex: RHException): ResponseEntity<ApiError> {
        val status = ErrorMapper.toStatus(ex.errorType)
        val body = ApiError(
            code = ex.errorType,
            message = ex.errorMessage,
            status = status.value()
        )
        return ResponseEntity.status(status).body(body)
    }
}
