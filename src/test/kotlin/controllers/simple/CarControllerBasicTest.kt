package controllers.simple

import com.github.michaelbull.result.Ok
import com.yb.rh.common.Countries
import com.yb.rh.controllers.CarController
import com.yb.rh.entities.CarDTO
import com.yb.rh.services.CarService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * Simple test for CarsController with minimal mocking
 */
class CarControllerBasicTest {

    @Test
    fun `findAll should return all cars from service`() {
        // Given
        val carService = mockk<CarService>()
        val carController = CarController(carService)
        
        val testCarDTO = mockk<CarDTO>()
        val carsList = listOf(testCarDTO)
        
        every { carService.findAll() } returns emptyList()
        
        // When
        val response = carController.findAll()
        
        // Then
        assertEquals(emptyList<CarDTO>(), response)
        verify { carService.findAll() }
    }
    
    @Test
    fun `findByPlateNumber should return car from service when found`() {
        // Given
        val carService = mockk<CarService>()
        val carController = CarController(carService)
        
        val testCarDTO = mockk<CarDTO>()
        val plateNumber = "TEST123"
        
        every { carService.getCarOrCreateRequest(plateNumber, Countries.IL) } returns Ok(testCarDTO)
        
        // When
        val response = carController.findByPlateNumber(plateNumber, Countries.IL)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { carService.getCarOrCreateRequest(plateNumber, Countries.IL) }
    }
} 