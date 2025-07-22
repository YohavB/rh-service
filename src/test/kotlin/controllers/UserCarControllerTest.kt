package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.services.MainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        val userCarRequestDTO = TestObjectBuilder.getUserCarRequestDTO()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.createUserCar(userCarRequestDTO) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user.id, result.user.id)
        assertEquals(userCarsDTO.cars.size, result.cars.size)
        verify { mainService.createUserCar(userCarRequestDTO) }
    }

    @Test
    fun `test getUserCarsByUserId success`() {
        // Given
        val userId = 1L
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.getUserCarsByUser(userId) } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId(userId)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user.id, result.user.id)
        assertEquals(userCarsDTO.cars.size, result.cars.size)
        verify { mainService.getUserCarsByUser(userId) }
    }

    @Test
    fun `test deleteUserCar success`() {
        // Given
        val userCarRequestDTO = TestObjectBuilder.getUserCarRequestDTO()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { mainService.deleteUserCar(userCarRequestDTO) } returns userCarsDTO

        // When
        val result = userCarController.deleteUserCar(userCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(userCarsDTO.user.id, result.user.id)
        assertEquals(userCarsDTO.cars.size, result.cars.size)
        verify { mainService.deleteUserCar(userCarRequestDTO) }
    }

    @Test
    fun `test createUserCar with different user and car`() {
        // Given
        val userCarRequestDTO = TestObjectBuilder.getUserCarRequestDTO(
            userId = 2L,
            carId = 3L
        )
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO(
            user = TestObjectBuilder.getUserDTO(id = 2L),
            cars = listOf(TestObjectBuilder.getCarDTO(id = 3L))
        )
        
        every { mainService.createUserCar(userCarRequestDTO) } returns userCarsDTO

        // When
        val result = userCarController.createUserCar(userCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(2L, result.user.id)
        assertEquals(1, result.cars.size)
        assertEquals(3L, result.cars[0].id)
        verify { mainService.createUserCar(userCarRequestDTO) }
    }

    @Test
    fun `test getUserCarsByUserId with different user`() {
        // Given
        val userId = 5L
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO(
            user = TestObjectBuilder.getUserDTO(id = 5L),
            cars = TestObjectBuilder.getMultipleCars(3).map { TestObjectBuilder.getCarDTO(id = it.id) }
        )
        
        every { mainService.getUserCarsByUser(userId) } returns userCarsDTO

        // When
        val result = userCarController.getUserCarsByUserId(userId)

        // Then
        assertNotNull(result)
        assertEquals(5L, result.user.id)
        assertEquals(3, result.cars.size)
        verify { mainService.getUserCarsByUser(userId) }
    }
} 