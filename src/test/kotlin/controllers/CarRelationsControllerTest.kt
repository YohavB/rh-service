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

class CarRelationsControllerTest {
    private lateinit var mainService: MainService
    private lateinit var carRelationsController: CarRelationsController

    @BeforeEach
    fun setUp() {
        mainService = mockk()
        carRelationsController = CarRelationsController(mainService)
    }

    @Test
    fun `test createCarsRelations success`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO()
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.createCarsRelations(carsRelationRequest) } returns listOf(carRelationsDTO)

        // When
        val result = carRelationsController.createCarsRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result[0].car)
        assertEquals(carRelationsDTO.isBlocking, result[0].isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result[0].isBlockedBy)
        verify { mainService.createCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test createCarsRelations with different car ids`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO(blockingCarId = 5L, blockedCarId = 10L)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.createCarsRelations(carsRelationRequest) } returns listOf(carRelationsDTO)

        // When
        val result = carRelationsController.createCarsRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result[0].car)
        assertEquals(carRelationsDTO.isBlocking, result[0].isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result[0].isBlockedBy)
        verify { mainService.createCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test createCarsRelations failure - service throws exception`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO()
        
        every { mainService.createCarsRelations(carsRelationRequest) } throws RHException("Cars not found")

        // When & Then
        assertThrows<RHException> {
            carRelationsController.createCarsRelations(carsRelationRequest)
        }
        verify { mainService.createCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test getCarRelationsByCarId success`() {
        // Given
        val carId = 1L
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.getCarRelationsByCarId(carId) } returns carRelationsDTO

        // When
        val result = carRelationsController.getCarRelationsByCarId(carId)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test getCarRelationsByCarId with different car id`() {
        // Given
        val carId = 999L
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.getCarRelationsByCarId(carId) } returns carRelationsDTO

        // When
        val result = carRelationsController.getCarRelationsByCarId(carId)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test getCarRelationsByCarId with zero car id`() {
        // Given
        val carId = 0L
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.getCarRelationsByCarId(carId) } returns carRelationsDTO

        // When
        val result = carRelationsController.getCarRelationsByCarId(carId)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test getCarRelationsByCarId failure - service throws exception`() {
        // Given
        val carId = 1L
        
        every { mainService.getCarRelationsByCarId(carId) } throws RHException("Car not found")

        // When & Then
        assertThrows<RHException> {
            carRelationsController.getCarRelationsByCarId(carId)
        }
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteCarRelations success`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO()
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequest) } returns carRelationsDTO

        // When
        val result = carRelationsController.deleteCarRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.deleteCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test deleteCarRelations with different car ids`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO(blockingCarId = 15L, blockedCarId = 20L)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequest) } returns carRelationsDTO

        // When
        val result = carRelationsController.deleteCarRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.deleteCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test deleteCarRelations failure - service throws exception`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequest) } throws RHException("Car relation not found")

        // When & Then
        assertThrows<RHException> {
            carRelationsController.deleteCarRelations(carsRelationRequest)
        }
        verify { mainService.deleteCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId success`() {
        // Given
        val carId = 1L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } returns Unit

        // When
        carRelationsController.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId with different car id`() {
        // Given
        val carId = 500L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } returns Unit

        // When
        carRelationsController.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId with zero car id`() {
        // Given
        val carId = 0L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } returns Unit

        // When
        carRelationsController.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId failure - service throws exception`() {
        // Given
        val carId = 1L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } throws RHException("Car not found")

        // When & Then
        assertThrows<RHException> {
            carRelationsController.deleteAllCarRelationsByCarId(carId)
        }
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }

    @Test
    fun `test createCarsRelations with negative car ids`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO(blockingCarId = -1L, blockedCarId = -2L)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.createCarsRelations(carsRelationRequest) } returns listOf(carRelationsDTO)

        // When
        val result = carRelationsController.createCarsRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result[0].car)
        assertEquals(carRelationsDTO.isBlocking, result[0].isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result[0].isBlockedBy)
        verify { mainService.createCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test getCarRelationsByCarId with negative car id`() {
        // Given
        val carId = -1L
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.getCarRelationsByCarId(carId) } returns carRelationsDTO

        // When
        val result = carRelationsController.getCarRelationsByCarId(carId)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteCarRelations with negative car ids`() {
        // Given
        val carsRelationRequest = TestObjectBuilder.getCarsRelationRequestDTO(blockingCarId = -5L, blockedCarId = -10L)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequest) } returns carRelationsDTO

        // When
        val result = carRelationsController.deleteCarRelations(carsRelationRequest)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car, result.car)
        assertEquals(carRelationsDTO.isBlocking, result.isBlocking)
        assertEquals(carRelationsDTO.isBlockedBy, result.isBlockedBy)
        verify { mainService.deleteCarsRelations(carsRelationRequest) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId with negative car id`() {
        // Given
        val carId = -1L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } returns Unit

        // When
        carRelationsController.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }
} 