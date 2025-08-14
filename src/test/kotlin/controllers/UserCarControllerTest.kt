package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.error.RHException
import com.yb.rh.services.MainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserCarControllerTest {
    private lateinit var mainService: MainService
    private lateinit var userCarController: UserCarController

    @BeforeEach
    fun setUp() {
        mainService = mockk()
        userCarController = UserCarController(mainService)
    }

    @Test
    fun `test createUserCar success`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.createUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.createUserCar(userCarRequest) }
    }

    @Test
    fun `test createUserCar with different user and car ids`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO(userId = 2L, carId = 3L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.createUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.createUserCar(userCarRequest) }
    }

    @Test
    fun `test createUserCar failure - service throws exception`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO()
        
        every { mainService.createUserCar(userCarRequest) } throws RHException("User or car not found")

        // When & Then
        assertThrows<RHException> {
            userCarController.createUserCar(userCarRequest)
        }
        verify { mainService.createUserCar(userCarRequest) }
    }

    @Test
    fun `test getUserCarsByUserId success`() {
        // Given
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.getUserCarsByUser() } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId()

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.getUserCarsByUser() }
    }

    @Test
    fun `test getUserCarsByUserId with different user id`() {
        // Given
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.getUserCarsByUser() } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId()

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.getUserCarsByUser() }
    }

    @Test
    fun `test getUserCarsByUserId with zero user id`() {
        // Given
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.getUserCarsByUser() } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId()

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.getUserCarsByUser() }
    }

    @Test
    fun `test getUserCarsByUserId failure - service throws exception`() {
        // Given
        
        every { mainService.getUserCarsByUser() } throws RHException("User not found")

        // When & Then
        assertThrows<RHException> {
            userCarController.getUserCarsByUserId()
        }
        verify { mainService.getUserCarsByUser() }
    }

    @Test
    fun `test deleteUserCar success`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.deleteUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.deleteUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.deleteUserCar(userCarRequest) }
    }

    @Test
    fun `test deleteUserCar with different user and car ids`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO(userId = 5L, carId = 10L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.deleteUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.deleteUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.deleteUserCar(userCarRequest) }
    }

    @Test
    fun `test deleteUserCar failure - service throws exception`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO()
        
        every { mainService.deleteUserCar(userCarRequest) } throws RHException("User car relationship not found")

        // When & Then
        assertThrows<RHException> {
            userCarController.deleteUserCar(userCarRequest)
        }
        verify { mainService.deleteUserCar(userCarRequest) }
    }

    @Test
    fun `test createUserCar with negative user id`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO(userId = -1L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.createUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.createUserCar(userCarRequest) }
    }

    @Test
    fun `test createUserCar with negative car id`() {
        // Given
        val userCarRequest = TestObjectBuilder.getUserCarRequestDTO(carId = -1L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.createUserCar(userCarRequest) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequest)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.createUserCar(userCarRequest) }
    }

    @Test
    fun `test getUserCarsByUserId with negative user id`() {
        // Given
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.getUserCarsByUser() } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId()

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user, result.user)
        assertEquals(userCarsDTO.cars, result.cars)
        verify { mainService.getUserCarsByUser() }
    }
} 