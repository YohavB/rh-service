package controllers.simple

import com.github.michaelbull.result.Ok
import com.yb.rh.common.UserStatus
import com.yb.rh.controllers.UsersCarsController
import com.yb.rh.entities.UsersCars
import com.yb.rh.entities.UsersCarsDTO
import com.yb.rh.services.UserCarService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * Simple test for UsersCarsController with minimal mocking
 */
class UsersCarsControllerBasicTest {

    @Test
    fun `findAll should return all users-cars relationships from service`() {
        // Given
        val userCarService = mockk<UserCarService>()
        val usersCarsController = UsersCarsController(userCarService)
        
        val testUsersCars = mockk<UsersCars>()
        val usersCarsList: MutableIterable<UsersCars> = mutableListOf(testUsersCars)
        
        every { userCarService.getAllUsersCars() } returns usersCarsList
        
        // When
        val response = usersCarsController.findAll()
        
        // Then
        assertEquals(usersCarsList, response)
        verify { userCarService.getAllUsersCars() }
    }
    
    @Test
    fun `findByPlateNumber should return users-cars by plate number`() {
        // Given
        val userCarService = mockk<UserCarService>()
        val usersCarsController = UsersCarsController(userCarService)
        
        val testUsersCarsDTO = mockk<UsersCarsDTO>()
        val plateNumber = "TEST123"
        val usersCarsListDTO = listOf(testUsersCarsDTO)
        
        every { userCarService.getUsersCarsByPlateNumber(plateNumber) } returns Ok(usersCarsListDTO)
        
        // When
        val response = usersCarsController.findByPlateNumber(plateNumber)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { userCarService.getUsersCarsByPlateNumber(plateNumber) }
    }
    
    @Test
    fun `findByUserId should return users-cars by user ID`() {
        // Given
        val userCarService = mockk<UserCarService>()
        val usersCarsController = UsersCarsController(userCarService)
        
        val testUsersCarsDTO = mockk<UsersCarsDTO>()
        val userId = 1L
        val usersCarsListDTO = listOf(testUsersCarsDTO)
        
        every { userCarService.getUsersCarsByUserId(userId) } returns Ok(usersCarsListDTO)
        
        // When
        val response = usersCarsController.findByUserId(userId)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { userCarService.getUsersCarsByUserId(userId) }
    }
    
    @Test
    fun `updateBlockedCarByPlateNumber should update blocked car status`() {
        // Given
        val userCarService = mockk<UserCarService>()
        val usersCarsController = UsersCarsController(userCarService)
        
        val blockingCarPlate = "BLOCKING123"
        val blockedCarPlate = "BLOCKED123"
        val userId = 1L
        val userStatus = UserStatus.BLOCKING
        
        every { 
            userCarService.updateBlockedCar(
                blockingCarPlate, 
                blockedCarPlate, 
                userId, 
                userStatus
            ) 
        } returns Ok(Unit)
        
        // When
        val response = usersCarsController.updateBlockedCarByPlateNumber(
            blockingCarPlate,
            blockedCarPlate,
            userId,
            userStatus
        )
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { 
            userCarService.updateBlockedCar(
                blockingCarPlate, 
                blockedCarPlate, 
                userId, 
                userStatus
            ) 
        }
    }
} 