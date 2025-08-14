package com.yb.rh.utils

import com.yb.rh.error.ApiError
import com.yb.rh.error.ErrorMapper
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

    // RHErrorResponse removed. New tests focus on ApiError from handler.

    @Test
    fun `test mapRHErrorToResponse for ENTITY_NOT_FOUND`() {
        // Given
        val exception = RHException("Entity not found", ErrorType.ENTITY_NOT_FOUND)

        // When
        val status = ErrorMapper.toStatus(ErrorType.ENTITY_NOT_FOUND)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.ENTITY_NOT_FOUND, "Entity not found", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Entity not found", errorResponse.message)
        assertEquals(ErrorType.ENTITY_NOT_FOUND, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for RESOURCE_NOT_EXISTS`() {
        // Given
        val exception = RHException("Resource not found", ErrorType.RESOURCE_NOT_EXISTS)

        // When
        val status = ErrorMapper.toStatus(ErrorType.RESOURCE_NOT_EXISTS)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.RESOURCE_NOT_EXISTS, "Resource not found", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Resource not found", errorResponse.message)
        assertEquals(ErrorType.RESOURCE_NOT_EXISTS, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for INVALID_INPUT`() {
        // Given
        val exception = RHException("Invalid input", ErrorType.INVALID_INPUT)

        // When
        val status = ErrorMapper.toStatus(ErrorType.INVALID_INPUT)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.INVALID_INPUT, "Invalid input", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Invalid input", errorResponse.message)
        assertEquals(ErrorType.INVALID_INPUT, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for RESOURCE_ALREADY_EXISTS`() {
        // Given
        val exception = RHException("Resource already exists", ErrorType.RESOURCE_ALREADY_EXISTS)

        // When
        val status = ErrorMapper.toStatus(ErrorType.RESOURCE_ALREADY_EXISTS)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.RESOURCE_ALREADY_EXISTS, "Resource already exists", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Resource already exists", errorResponse.message)
        assertEquals(ErrorType.RESOURCE_ALREADY_EXISTS, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for AUTHENTICATION`() {
        // Given
        val exception = RHException("Authentication failed", ErrorType.AUTHENTICATION)

        // When
        val status = ErrorMapper.toStatus(ErrorType.AUTHENTICATION)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.AUTHENTICATION, "Authentication failed", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Authentication failed", errorResponse.message)
        assertEquals(ErrorType.AUTHENTICATION, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for BAD_CREDENTIAL`() {
        // Given
        val exception = RHException("Bad credentials", ErrorType.BAD_CREDENTIAL)

        // When
        val status = ErrorMapper.toStatus(ErrorType.BAD_CREDENTIAL)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.BAD_CREDENTIAL, "Bad credentials", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Bad credentials", errorResponse.message)
        assertEquals(ErrorType.BAD_CREDENTIAL, errorResponse.code)
    }



    @Test
    fun `test mapRHErrorToResponse for HTTP_CALL`() {
        // Given
        val exception = RHException("HTTP call failed", ErrorType.HTTP_CALL)

        // When
        val status = ErrorMapper.toStatus(ErrorType.HTTP_CALL)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.HTTP_CALL, "HTTP call failed", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("HTTP call failed", errorResponse.message)
        assertEquals(ErrorType.HTTP_CALL, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for DB_ACCESS`() {
        // Given
        val exception = RHException("Database access failed", ErrorType.DB_ACCESS)

        // When
        val status = ErrorMapper.toStatus(ErrorType.DB_ACCESS)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.DB_ACCESS, "Database access failed", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Database access failed", errorResponse.message)
        assertEquals(ErrorType.DB_ACCESS, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for CORRUPTED_DATA`() {
        // Given
        val exception = RHException("Corrupted data", ErrorType.CORRUPTED_DATA)

        // When
        val status = ErrorMapper.toStatus(ErrorType.CORRUPTED_DATA)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.CORRUPTED_DATA, "Corrupted data", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Corrupted data", errorResponse.message)
        assertEquals(ErrorType.CORRUPTED_DATA, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for INVALID_JWT`() {
        // Given
        val exception = RHException("Invalid JWT", ErrorType.INVALID_JWT)

        // When
        val status = ErrorMapper.toStatus(ErrorType.INVALID_JWT)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.INVALID_JWT, "Invalid JWT", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Invalid JWT", errorResponse.message)
        assertEquals(ErrorType.INVALID_JWT, errorResponse.code)
    }

    @Test
    fun `test mapRHErrorToResponse for UNKNOWN`() {
        // Given
        val exception = RHException("Unknown error", ErrorType.UNKNOWN)

        // When
        val status = ErrorMapper.toStatus(ErrorType.UNKNOWN)
        val response = org.springframework.http.ResponseEntity.status(status).body(
            ApiError(ErrorType.UNKNOWN, "Unknown error", status.value())
        )

        // Then
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        val errorResponse = response.body as ApiError
        assertEquals("Unknown error", errorResponse.message)
        assertEquals(ErrorType.UNKNOWN, errorResponse.code)
    }
} 