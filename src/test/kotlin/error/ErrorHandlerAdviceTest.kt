package error

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.yb.rh.error.ErrorHandlerAdvice
import com.yb.rh.error.ErrorResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException

class ErrorHandlerAdviceTest {

    private val errorHandlerAdvice = ErrorHandlerAdvice()

    @Test
    fun `test handleMethodArgumentNotValid returns appropriate error responses`() {
        // Mock dependencies
        val bindingResult = mockk<BindingResult>()
        val fieldError = FieldError(
            "testObject", "testField", "testValue", 
            false, null, null, "must not be null"
        )
        val fieldErrors = listOf(fieldError)
        
        val exception = mockk<MethodArgumentNotValidException>()
        every { exception.bindingResult } returns bindingResult
        every { bindingResult.fieldErrors } returns fieldErrors
        
        // Execute the method
        val errorResponses = errorHandlerAdvice.handleMethodArgumentNotValid(exception)
        
        // Verify result
        assertEquals(1, errorResponses?.size)
        val errorResponse = errorResponses?.get(0)
        assertEquals("Value testValue for `testField` must not be null", errorResponse?.cause)
    }
    
    @Test
    fun `test handleMissingParameter returns appropriate error response`() {
        // Mock the exception directly without using the inner Parameter class
        val exception = mockk<MissingKotlinParameterException>()
        
        // Just mock the behavior we need
        every { exception.parameter.name } returns "testParam"
        
        // Execute the method
        val errorResponse = errorHandlerAdvice.handleMissingParameter(exception)
        
        // Verify result
        assertEquals("parameter `testParam` is missing from request body", errorResponse.cause)
    }
    
    @Test
    fun `test handleMissingRequestHeader returns appropriate error response`() {
        // Mock the exception
        val exception = mockk<MissingRequestHeaderException>()
        every { exception.headerName } returns "X-Test-Header"
        
        // Execute the method
        val errorResponse = errorHandlerAdvice.handleMissingRequestHeader(exception)
        
        // Verify result
        assertEquals("parameter `X-Test-Header` is missing from request header", errorResponse.cause)
    }
    
    @Test
    fun `test ErrorResponse factory method`() {
        // Test with a message
        val errorWithMessage = ErrorResponse.Factory.withErrorMessage("test error")
        assertEquals("test error", errorWithMessage.cause)
        
        // Test with null message
        val errorWithNullMessage = ErrorResponse.Factory.withErrorMessage(null)
        assertEquals("no error message provided", errorWithNullMessage.cause)
    }
} 