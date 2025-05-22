package error

import com.yb.rh.error.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ErrorTypeTest {

    @Test
    fun `ErrorType enum contains expected values`() {
        // Verify that the enum contains the expected values
        val expectedValues = setOf(
            "AUTHENTICATION",
            "INVALID_JWT",
            "CORRUPTED_DATA",
            "DB_ACCESS",
            "HTTP_CALL",
            "RESOURCE_ALREADY_EXISTS",
            "RESOURCE_NOT_EXISTS",
            "INVALID_INPUT",
            "UNKNOWN",
            "ENTITY_NOT_FOUND",
            "ILLEGAL_APPLICATION_STATE",
            "FEATURE_DISABLED",
            "UPDATE_EMAIL_TRACKING_STATUS_FAILED",
            "SQS_PRODUCE_FAILED",
            "BAD_CREDENTIAL"
        )
        
        // Convert enum values to a set of names
        val actualValues = ErrorType.values().map { it.name }.toSet()
        
        // Verify all expected values are present
        assertEquals(expectedValues, actualValues)
        
        // Verify count matches
        assertEquals(expectedValues.size, ErrorType.values().size)
    }
    
    @Test
    fun `ErrorType values can be accessed by name`() {
        // Verify we can access enum values by name
        assertEquals(ErrorType.AUTHENTICATION, ErrorType.valueOf("AUTHENTICATION"))
        assertEquals(ErrorType.INVALID_JWT, ErrorType.valueOf("INVALID_JWT"))
        assertEquals(ErrorType.RESOURCE_NOT_EXISTS, ErrorType.valueOf("RESOURCE_NOT_EXISTS"))
        assertEquals(ErrorType.UNKNOWN, ErrorType.valueOf("UNKNOWN"))
    }
    
    @Test
    fun `ErrorType values have unique ordinals`() {
        // Get all enum values
        val values = ErrorType.values()
        
        // Create a set of ordinals
        val ordinals = values.map { it.ordinal }.toSet()
        
        // Verify that the number of unique ordinals equals the number of enum values
        assertEquals(values.size, ordinals.size)
    }
    
    @Test
    fun `test RHException construction`() {
        val errorMessage = "Test error message"
        val errorType = ErrorType.INVALID_INPUT
        val throwable = RuntimeException("Cause")
        
        val exception = RHException(errorMessage, errorType, throwable)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(errorType, exception.errorType)
        assertEquals(throwable, exception.cause)
    }
    
    @Test
    fun `test HttpException construction`() {
        val errorMessage = "Test HTTP error"
        val errorType = ErrorType.HTTP_CALL
        val httpStatus = HttpStatus.BAD_REQUEST_400
        
        val exception = HttpException(errorMessage, null, errorType, httpStatus)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(errorType, exception.errorType)
        assertEquals(httpStatus, exception.httpStatus)
    }
    
    @Test
    fun `test WithErrorMessage construction and methods`() {
        val clientName = ClientName.RHSERVICE
        val requestType = "TEST_REQUEST"
        val httpStatus = HttpStatus.BAD_REQUEST_400
        val msg = "Bad request error"
        val errorType = ErrorType.INVALID_INPUT
        
        val errorWithMessage = WithErrorMessage(clientName, requestType, httpStatus, msg, errorType)
        
        assertEquals(clientName, errorWithMessage.clientName)
        assertEquals(requestType, errorWithMessage.requestType)
        assertEquals(httpStatus, errorWithMessage.httpStatus)
        assertEquals(msg, errorWithMessage.msg)
        assertEquals(errorType, errorWithMessage.errorType)
        
        val logMessage = errorWithMessage.toLogMessage()
        assertNotNull(logMessage)
    }
    
    @Test
    fun `test EntityNotFound construction`() {
        val entityClass = User::class.java
        val entityId = "123"
        
        val exception = EntityNotFound(entityClass, entityId)
        
        assertEquals("${entityClass.name} Entity with Id : $entityId Not Found", exception.message)
        assertEquals(ErrorType.ENTITY_NOT_FOUND, exception.errorType)
    }
    
    @Test
    fun `test InvalidCarData construction`() {
        val errorMessage = "Invalid car data"
        
        val exception = InvalidCarData(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.INVALID_INPUT, exception.errorType)
    }
    
    @Test
    fun `test ExternalServiceErrors enum`() {
        val errors = ExternalServiceErrors.values()
        
        // Verify count
        assertEquals(5, errors.size)
        
        // Test the map lookup functionality
        val displayName = "alliance with given sunbitKey wasn't found or is disabled"
        val httpStatus = ExternalServiceErrors.fromErrDisplayNameToHttpStatus(displayName)
        
        assertEquals(401, httpStatus)
        
        // Test unknown display name returns 500
        val unknownStatus = ExternalServiceErrors.fromErrDisplayNameToHttpStatus("unknown error")
        assertEquals(500, unknownStatus)
    }
    
    // Helper class for testing EntityNotFound
    private class User
} 