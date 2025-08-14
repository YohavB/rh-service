package com.yb.rh.error

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
        val body = response.body as ApiError
        assertEquals(ErrorType.INVALID_INPUT, body.code)
        assertEquals(422, body.status)
        assert(body.message.contains("field: default message"))
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
        val body = response.body as ApiError
        assertEquals(ErrorType.INVALID_INPUT, body.code)
        assertEquals(422, body.status)
        assertEquals("parameter `Authorization` is missing from request header", body.message)
    }

} 