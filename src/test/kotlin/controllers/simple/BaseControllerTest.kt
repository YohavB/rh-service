package controllers.simple

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.controllers.BaseController
import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import com.yb.rh.utils.SuccessResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * Simple test for BaseController
 */
class BaseControllerTest {

    class TestBaseController : BaseController()

    @Test
    fun `handleServiceResult should return success response on success`() {
        // Given
        val controller = TestBaseController()
        val testData = "Test Data"
        val successResult = Ok(testData)
        
        // When
        val response = controller.handleServiceResult(
            successResult,
            "Success message",
            "Failure message"
        )
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(testData, (response.body as SuccessResponse<*>).entity)
    }
    
    @Test
    fun `handleServiceResult should return error response on failure`() {
        // Given
        val controller = TestBaseController()
        val errorMessage = "Test error message"
        val errorResult = Err(RHException(errorMessage, ErrorType.INVALID_INPUT))
        
        // When
        val response = controller.handleServiceResult(
            errorResult,
            "Success message",
            "Failure message"
        )
        
        // Then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
    }
} 