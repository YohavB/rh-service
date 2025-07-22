package com.yb.rh.error

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus


open class RHResponse

class ErrorResponse(val cause: String, val errorCode: Int? = null) : RHResponse() {
    companion object Factory {
        fun withErrorMessage(message: String?) = ErrorResponse(message ?: "no error message provided")
    }
}

@ControllerAdvice
class ErrorHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): List<ErrorResponse>? =
        ex.bindingResult.fieldErrors.map {
            ErrorResponse("Value ${it.rejectedValue} for `${it.field}` ${it.defaultMessage}")
        }

    @ExceptionHandler(MissingKotlinParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMissingParameter(ex: MissingKotlinParameterException): ErrorResponse =
        ErrorResponse("parameter `${ex.parameter.name}` is missing from request body")


    @ExceptionHandler(MissingRequestHeaderException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMissingRequestHeader(ex: MissingRequestHeaderException): ErrorResponse =
        ErrorResponse("parameter `${ex.headerName}` is missing from request header")

    @ExceptionHandler(RHException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleRHException(ex: RHException): ErrorResponse =
        ErrorResponse(ex.message ?: "Unknown error occurred")
}
