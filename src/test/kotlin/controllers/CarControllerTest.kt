package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.services.CarService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CarControllerTest {
    private lateinit var carService: CarService
    private lateinit var carController: CarController

    @BeforeEach
    fun setUp() {
        carService = mockk()
        carController = CarController(carService)
    }

    @Test
    fun `test findCarRequest success`() {
        // Given
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO()
        val carDTO = TestObjectBuilder.getCarDTO()
        
        every { carService.getCarOrCreateRequest(findCarRequestDTO) } returns carDTO

        // When
        val result = carController.findCarRequest(findCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        assertEquals(carDTO.brand, result.brand)
        assertEquals(carDTO.model, result.model)
        assertEquals(carDTO.color, result.color)
        verify { carService.getCarOrCreateRequest(findCarRequestDTO) }
    }

    @Test
    fun `test findCarRequest with different plate number`() {
        // Given
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO(
            plateNumber = "XYZ789"
        )
        val carDTO = TestObjectBuilder.getCarDTO(
            plateNumber = "XYZ789"
        )
        
        every { carService.getCarOrCreateRequest(findCarRequestDTO) } returns carDTO

        // When
        val result = carController.findCarRequest(findCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals("XYZ789", result.plateNumber)
        verify { carService.getCarOrCreateRequest(findCarRequestDTO) }
    }

    @Test
    fun `test findCarRequest with different country`() {
        // Given
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO(
            country = com.yb.rh.common.Countries.IL
        )
        val carDTO = TestObjectBuilder.getCarDTO(
            country = com.yb.rh.common.Countries.IL
        )
        
        every { carService.getCarOrCreateRequest(findCarRequestDTO) } returns carDTO

        // When
        val result = carController.findCarRequest(findCarRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(com.yb.rh.common.Countries.IL, result.country)
        verify { carService.getCarOrCreateRequest(findCarRequestDTO) }
    }
} 