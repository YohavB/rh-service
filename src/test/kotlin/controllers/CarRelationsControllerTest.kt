package com.yb.rh.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.UserCarSituation
import com.yb.rh.services.MainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CarRelationsControllerTest {
    private lateinit var mainService: MainService
    private lateinit var objectMapper: ObjectMapper
    private lateinit var carRelationsController: CarRelationsController

    @BeforeEach
    fun setUp() {
        mainService = mockk()
        objectMapper = ObjectMapper()
        carRelationsController = CarRelationsController(mainService)
    }

    @Test
    fun `test createCarsRelations success`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO()
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.createCarsRelations(carsRelationRequestDTO) } returns carRelationsDTO

        // When
        val result = carRelationsController.createCarsRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        assertEquals(carRelationsDTO.isBlocking.size, result.isBlocking.size)
        assertEquals(carRelationsDTO.isBlockedBy.size, result.isBlockedBy.size)
        verify { mainService.createCarsRelations(carsRelationRequestDTO) }
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
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteCarRelations success`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO()
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequestDTO) } returns carRelationsDTO

        // When
        val result = carRelationsController.deleteCarRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { mainService.deleteCarsRelations(carsRelationRequestDTO) }
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
    fun `test createCarsRelations with IS_BLOCKED situation`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            userCarSituation = UserCarSituation.IS_BLOCKED
        )
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.createCarsRelations(carsRelationRequestDTO) } returns carRelationsDTO

        // When
        val result = carRelationsController.createCarsRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { mainService.createCarsRelations(carsRelationRequestDTO) }
    }

    @Test
    fun `test getCarRelationsByCarId with different car`() {
        // Given
        val carId = 5L
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 5L)
        )
        
        every { mainService.getCarRelationsByCarId(carId) } returns carRelationsDTO

        // When
        val result = carRelationsController.getCarRelationsByCarId(carId)

        // Then
        assertNotNull(result)
        assertEquals(5L, result.car.id)
        verify { mainService.getCarRelationsByCarId(carId) }
    }

    @Test
    fun `test deleteCarRelations with different cars`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 3L,
            blockedCarId = 4L
        )
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        
        every { mainService.deleteCarsRelations(carsRelationRequestDTO) } returns carRelationsDTO

        // When
        val result = carRelationsController.deleteCarRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { mainService.deleteCarsRelations(carsRelationRequestDTO) }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId with different car`() {
        // Given
        val carId = 10L
        
        every { mainService.deleteAllCarRelationsByCarId(carId) } returns Unit

        // When
        carRelationsController.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { mainService.deleteAllCarRelationsByCarId(carId) }
    }
} 