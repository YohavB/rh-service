package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.enum.Countries
import com.yb.rh.error.RHException
import com.yb.rh.services.CarService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO()
        val carDTO = TestObjectBuilder.getCarDTO()
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        assertEquals(carDTO.country, result.country)
        assertEquals(carDTO.brand, result.brand)
        assertEquals(carDTO.model, result.model)
        assertEquals(carDTO.color, result.color)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with different country`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(country = Countries.IL)
        val carDTO = TestObjectBuilder.getCarDTO(country = Countries.IL)
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.country, result.country)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with different plate number`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = "XYZ789")
        val carDTO = TestObjectBuilder.getCarDTO(plateNumber = "XYZ789")
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with userId`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(userId = 1L)
        val carDTO = TestObjectBuilder.getCarDTO()
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with null userId`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(userId = null)
        val carDTO = TestObjectBuilder.getCarDTO()
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest failure - service throws exception`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO()
        
        every { carService.getCarOrCreateRequest(requestDTO) } throws RHException("Car not found")

        // When & Then
        assertThrows<RHException> {
            carController.getCarOrCreateRequest(requestDTO)
        }
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with empty plate number`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = "")
        val carDTO = TestObjectBuilder.getCarDTO(plateNumber = "")
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with special characters in plate number`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = "ABC-123")
        val carDTO = TestObjectBuilder.getCarDTO(plateNumber = "ABC-123")
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }

    @Test
    fun `test findCarRequest with long plate number`() {
        // Given
        val requestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = "VERYLONGPLATENUMBER123")
        val carDTO = TestObjectBuilder.getCarDTO(plateNumber = "VERYLONGPLATENUMBER123")
        
        every { carService.getCarOrCreateRequest(requestDTO) } returns carDTO

        // When
        val result = carController.getCarOrCreateRequest(requestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        verify { carService.getCarOrCreateRequest(requestDTO) }
    }
} 