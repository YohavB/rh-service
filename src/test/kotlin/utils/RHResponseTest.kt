package com.yb.rh.utils

import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RHResponseTest {

    @Test
    fun `test SuccessResponse constructor and getter`() {
        // Given
        val testData = "test data"

        // When
        val successResponse = SuccessResponse(testData)

        // Then
        assertNotNull(successResponse)
        assertEquals(testData, successResponse.entity)
    }

    @Test
    fun `test ErrorResponse constructor with cause only`() {
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
    fun `test ErrorResponse constructor with cause and error code`() {
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

    @Test
    fun `test mapRHErrorToResponse for ENTITY_NOT_FOUND`() {
        // Given
        val exception = RHException("Entity not found", ErrorType.ENTITY_NOT_FOUND)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Entity not found", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for RESOURCE_NOT_EXISTS`() {
        // Given
        val exception = RHException("Resource not found", ErrorType.RESOURCE_NOT_EXISTS)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Resource not found", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for INVALID_INPUT`() {
        // Given
        val exception = RHException("Invalid input", ErrorType.INVALID_INPUT)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Invalid input", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for RESOURCE_ALREADY_EXISTS`() {
        // Given
        val exception = RHException("Resource already exists", ErrorType.RESOURCE_ALREADY_EXISTS)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Resource already exists", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for AUTHENTICATION`() {
        // Given
        val exception = RHException("Authentication failed", ErrorType.AUTHENTICATION)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Authentication failed", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for BAD_CREDENTIAL`() {
        // Given
        val exception = RHException("Bad credentials", ErrorType.BAD_CREDENTIAL)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Bad credentials", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }



    @Test
    fun `test mapRHErrorToResponse for HTTP_CALL`() {
        // Given
        val exception = RHException("HTTP call failed", ErrorType.HTTP_CALL)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("HTTP call failed", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for DB_ACCESS`() {
        // Given
        val exception = RHException("Database access failed", ErrorType.DB_ACCESS)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Database access failed", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for CORRUPTED_DATA`() {
        // Given
        val exception = RHException("Corrupted data", ErrorType.CORRUPTED_DATA)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Corrupted data", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for INVALID_JWT`() {
        // Given
        val exception = RHException("Invalid JWT", ErrorType.INVALID_JWT)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Invalid JWT", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }

    @Test
    fun `test mapRHErrorToResponse for UNKNOWN`() {
        // Given
        val exception = RHException("Unknown error", ErrorType.UNKNOWN)

        // When
        val response = Utils.mapRHErrorToResponse(exception)

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        val errorResponse = response.body as ErrorResponse
        assertEquals("Unknown error", errorResponse.cause)
        assertEquals(null, errorResponse.errorCode)
    }
} 