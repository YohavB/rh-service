package error

import com.yb.rh.error.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ErrorsTest {

    @Test
    fun `test InternalServerError construction`() {
        val errorMessage = "Internal server error message"
        val exception = InternalServerError(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.UNKNOWN, exception.errorType)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR_500, exception.httpStatus)
    }
    
    @Test
    fun `test NoResponseException construction`() {
        val requestType = "GET_USER"
        val exception = NoResponseException(requestType)
        
        assertEquals("Call for `$requestType` didn't get a response", exception.message)
        assertEquals(ErrorType.HTTP_CALL, exception.errorType)
        assertEquals(HttpStatus.REQUEST_TIMEOUT_408, exception.httpStatus)
        
        // Test with custom http status
        val customStatus = HttpStatus.GATEWAY_TIMEOUT_504
        val exceptionWithCustomStatus = NoResponseException(requestType, customStatus)
        assertEquals(customStatus, exceptionWithCustomStatus.httpStatus)
    }
    
    @Test
    fun `test NoResponseBody construction`() {
        val call = "GET_USER_DETAILS"
        val exception = NoResponseBody(call)
        
        assertEquals("`$call` has no body. Http Status Code: ${HttpStatus.OK_200}", exception.message)
        assertEquals(ErrorType.HTTP_CALL, exception.errorType)
        assertEquals(HttpStatus.OK_200, exception.httpStatus)
        
        // Test with custom http status
        val customStatus = HttpStatus.ACCEPTED_202
        val exceptionWithCustomStatus = NoResponseBody(call, customStatus)
        assertEquals(customStatus, exceptionWithCustomStatus.httpStatus)
    }
    
    @Test
    fun `test GetDbRecordFailed construction`() {
        val table = "users"
        val exception = GetDbRecordFailed(table)
        
        assertEquals("Failed to get a record from table `$table`", exception.message)
        assertEquals(ErrorType.DB_ACCESS, exception.errorType)
        assertEquals(table, exception.table)
    }
    
    @Test
    fun `test SaveDbRecordFailed construction`() {
        val table = "cars"
        val exception = SaveDbRecordFailed(table)
        
        assertEquals("Failed to save a record to table `$table`", exception.message)
        assertEquals(ErrorType.DB_ACCESS, exception.errorType)
        assertEquals(table, exception.table)
    }
    
    @Test
    fun `test ExternalApiError construction`() {
        val errorMessage = "External API error message"
        val exception = ExternalApiError(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.HTTP_CALL, exception.errorType)
    }
    
    @Test
    fun `test InvalidCarData construction`() {
        val errorMessage = "Invalid car data message"
        val exception = InvalidCarData(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.INVALID_INPUT, exception.errorType)
    }
    
    @Test
    fun `test InvalidUserData construction`() {
        val errorMessage = "Invalid user data message"
        val exception = InvalidUserData(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.INVALID_INPUT, exception.errorType)
    }
    
    @Test
    fun `test NotificationError construction`() {
        val errorMessage = "Notification error message"
        val exception = NotificationError(errorMessage)
        
        assertEquals(errorMessage, exception.message)
        assertEquals(ErrorType.HTTP_CALL, exception.errorType)
    }
    
    @Test
    fun `test EnrichmentError construction`() {
        val propertyName = "user"
        val step = "authentication"
        val exception = EnrichmentError(propertyName, step)
        
        assertEquals("Failed to enrich With $propertyName in $step", exception.message)
        assertEquals(ErrorType.UNKNOWN, exception.errorType)
    }
    
    @Test
    fun `test HttpErrorBody construction and toString`() {
        val code = "404"
        val message = "Not Found"
        val errorBody = HttpErrorBody(code, message)
        
        assertEquals(code, errorBody.code)
        assertEquals(message, errorBody.message)
        assertEquals(message, errorBody.toString())
        
        // Test with null message
        val errorBodyWithNullMessage = HttpErrorBody(code, null)
        assertEquals("", errorBodyWithNullMessage.toString())
    }
    
    @Test
    fun `test WithErrorMessage fromErrorMsg method`() {
        // Test authentication error
        val authErrorType = WithErrorMessage.fromErrorMsg("alliance with given sunbitKey wasn't found or is disabled")
        assertEquals(ErrorType.AUTHENTICATION, authErrorType)
        
        // Test resource already exists error
        val resourceExistsErrorType = WithErrorMessage.fromErrorMsg("Same location was already added for this alliance")
        assertEquals(ErrorType.RESOURCE_ALREADY_EXISTS, resourceExistsErrorType)
        
        // Test unknown error
        val unknownErrorType = WithErrorMessage.fromErrorMsg("Some unknown error")
        assertEquals(ErrorType.UNKNOWN, unknownErrorType)
    }
} 