package com.yb.rh.error

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.yb.rh.utils.ErrorResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ErrorHandlerAdviceTest {
    private lateinit var errorHandlerAdvice: ErrorHandlerAdvice

    @BeforeEach
    fun setUp() {
        errorHandlerAdvice = ErrorHandlerAdvice()
    }

    @Test
    fun `test handleMethodArgumentNotValid with field errors`() {
        // Given
        val fieldError = FieldError("object", "field", "rejectedValue", false, null, null, "default message")
        val bindingResult = mockk<BindingResult>()
        every { bindingResult.fieldErrors } returns listOf(fieldError)
        
        val methodParameter = mockk<MethodParameter>()
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)

        // When
        val response = errorHandlerAdvice.handleMethodArgumentNotValid(exception)

        // Then
        assertNotNull(response)
        assertEquals(1, response?.size)
        val errorResponse = response?.first()
        assertNotNull(errorResponse)
        assertEquals("Value rejectedValue for `field` default message", errorResponse.cause)
    }

    @Test
    fun `test handleMissingParameter`() {
        // Given
        val parameter = mockk<kotlin.reflect.KParameter>()
        every { parameter.name } returns "testParam"
        
        val exception = mockk<MissingKotlinParameterException>()
        every { exception.parameter } returns parameter

        // When
        val response = errorHandlerAdvice.handleMissingParameter(exception)

        // Then
        assertNotNull(response)
        assertEquals("parameter `testParam` is missing from request body", response.cause)
    }

    @Test
    fun `test handleMissingRequestHeader`() {
        // Given
        val headerName = "Authorization"
        val exception = mockk<MissingRequestHeaderException>()
        every { exception.headerName } returns headerName

        // When
        val response = errorHandlerAdvice.handleMissingRequestHeader(exception)

        // Then
        assertNotNull(response)
        assertEquals("parameter `Authorization` is missing from request header", response.cause)
    }

    @Test
    fun `test ErrorResponse constructor`() {
        // Given
        val cause = "Test error message"
        val errorCode = 404

        // When
        val errorResponse = ErrorResponse(cause, errorCode)

        // Then
        assertNotNull(errorResponse)
        assertEquals(cause, errorResponse.cause)
        assertEquals(errorCode, errorResponse.errorCode)
    }

    @Test
    fun `test ErrorResponse constructor without error code`() {
        // Given
        val cause = "Test error message"

        // When
        val errorResponse = ErrorResponse(cause)

        // Then
        assertNotNull(errorResponse)
        assertEquals(cause, errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test ErrorResponse Factory withErrorMessage with message`() {
        // Given
        val message = "Test error message"

        // When
        val errorResponse = ErrorResponse.withErrorMessage(message)

        // Then
        assertNotNull(errorResponse)
        assertEquals(message, errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test ErrorResponse Factory withErrorMessage with null`() {
        // When
        val errorResponse = ErrorResponse.withErrorMessage(null)

        // Then
        assertNotNull(errorResponse)
        assertEquals("no error message provided", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }
} 