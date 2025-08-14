package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.CarDTO
import com.yb.rh.repositories.CarsRelationsRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CarsRelationsServiceTest {

    private lateinit var carsRelationsRepository: CarsRelationsRepository
    private lateinit var carsRelationsService: CarsRelationsService

    @BeforeEach
    fun setUp() {
        carsRelationsRepository = mockk()
        carsRelationsService = CarsRelationsService(carsRelationsRepository)
    }

    @Test
    fun `test createCarsRelation success`() {
        // Given
        val blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER")
        val blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED")
        val carsRelation = TestObjectBuilder.getCarsRelations(blockingCar = blockingCar, blockedCar = blockedCar)

        every { carsRelationsRepository.findByBlockingCar(blockingCar) } returns emptyList()
        every { carsRelationsRepository.save(any()) } returns carsRelation

        // When
        carsRelationsService.createCarsRelation(blockingCar, blockedCar)

        // Then
        verify {
            carsRelationsRepository.findByBlockingCar(blockingCar)
            carsRelationsRepository.save(any())
        }
    }

    @Test
    fun `test createCarsRelation already exists`() {
        // Given
        val blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER")
        val blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED")
        val existingRelation = TestObjectBuilder.getCarsRelations(blockingCar = blockingCar, blockedCar = blockedCar)

        every { carsRelationsRepository.findByBlockingCar(blockingCar) } returns listOf(existingRelation)

        // When & Then
        assertThrows<com.yb.rh.error.RHException> {
            carsRelationsService.createCarsRelation(blockingCar, blockedCar)
        }
        verify { carsRelationsRepository.findByBlockingCar(blockingCar) }
        verify(exactly = 0) { carsRelationsRepository.save(any()) }
    }

    @Test
    fun `test findCarRelations success`() {
        // Given
        val car = TestObjectBuilder.getCar(plateNumber = "MAIN")
        val blockingCar1 = TestObjectBuilder.getCar(plateNumber = "BLOCKER1")
        val blockingCar2 = TestObjectBuilder.getCar(plateNumber = "BLOCKER2")
        val blockedCar1 = TestObjectBuilder.getCar(plateNumber = "BLOCKED1")
        val blockedCar2 = TestObjectBuilder.getCar(plateNumber = "BLOCKED2")

        val isBlockingRelations = listOf(
            TestObjectBuilder.getCarsRelations(blockingCar = car, blockedCar = blockedCar1),
            TestObjectBuilder.getCarsRelations(blockingCar = car, blockedCar = blockedCar2)
        )
        val isBlockedByRelations = listOf(
            TestObjectBuilder.getCarsRelations(blockingCar = blockingCar1, blockedCar = car),
            TestObjectBuilder.getCarsRelations(blockingCar = blockingCar2, blockedCar = car)
        )

        every { carsRelationsRepository.findByBlockingCar(car) } returns isBlockingRelations
        every { carsRelationsRepository.findByBlockedCar(car) } returns isBlockedByRelations

        // When
        val result = carsRelationsService.findCarRelations(car)

        // Then
        assertNotNull(result)
        assertEquals(car, result.car)
        assertEquals(2, result.isBlocking.size)
        assertEquals(2, result.isBlockedBy.size)
        assertTrue(result.isBlocking.contains(blockedCar1))
        assertTrue(result.isBlocking.contains(blockedCar2))
        assertTrue(result.isBlockedBy.contains(blockingCar1))
        assertTrue(result.isBlockedBy.contains(blockingCar2))
        verify {
            carsRelationsRepository.findByBlockingCar(car)
            carsRelationsRepository.findByBlockedCar(car)
        }
    }

    @Test
    fun `test findCarRelations empty`() {
        // Given
        val car = TestObjectBuilder.getCar(plateNumber = "MAIN")

        every { carsRelationsRepository.findByBlockingCar(car) } returns emptyList()
        every { carsRelationsRepository.findByBlockedCar(car) } returns emptyList()

        // When
        val result = carsRelationsService.findCarRelations(car)

        // Then
        assertNotNull(result)
        assertEquals(car, result.car)
        assertTrue(result.isBlocking.isEmpty())
        assertTrue(result.isBlockedBy.isEmpty())
        verify {
            carsRelationsRepository.findByBlockingCar(car)
            carsRelationsRepository.findByBlockedCar(car)
        }
    }

    @Test
    fun `test findCarRelationsDTO success`() {
        // Given
        val car = mockk<com.yb.rh.entities.Car>()
        val carDTO = TestObjectBuilder.getCarDTO()

        every { carsRelationsRepository.findByBlockingCar(car) } returns emptyList()
        every { carsRelationsRepository.findByBlockedCar(car) } returns emptyList()

        // When
        val result = carsRelationsService.findCarRelationsByCar(car)

        // Then
        assertNotNull(result)
        assertEquals(car, result.car)
        verify {
            carsRelationsRepository.findByBlockingCar(car)
            carsRelationsRepository.findByBlockedCar(car)
        }
    }

    @Test
    fun `test deleteSpecificCarsRelation success`() {
        // Given
        val blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER")
        val blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED")
        val relationToDelete = TestObjectBuilder.getCarsRelations(blockingCar = blockingCar, blockedCar = blockedCar)

        every { carsRelationsRepository.findByBlockingCar(blockingCar) } returns listOf(relationToDelete)
        every { carsRelationsRepository.delete(relationToDelete) } just Runs

        // When
        carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar)

        // Then
        verify {
            carsRelationsRepository.findByBlockingCar(blockingCar)
            carsRelationsRepository.delete(relationToDelete)
        }
    }

    @Test
    fun `test deleteSpecificCarsRelation not found`() {
        // Given
        val blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER")
        val blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED")

        every { carsRelationsRepository.findByBlockingCar(blockingCar) } returns emptyList()

        // When & Then
        assertThrows<com.yb.rh.error.RHException> {
            carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar)
        }
        verify { carsRelationsRepository.findByBlockingCar(blockingCar) }
        verify(exactly = 0) { carsRelationsRepository.delete(any()) }
    }

    @Test
    fun `test deleteAllCarsRelations success`() {
        // Given
        val car = TestObjectBuilder.getCar(plateNumber = "MAIN")
        val blockingRelations = listOf(
            TestObjectBuilder.getCarsRelations(
                blockingCar = car,
                blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED1")
            ),
            TestObjectBuilder.getCarsRelations(
                blockingCar = car,
                blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED2")
            )
        )
        val blockedByRelations = listOf(
            TestObjectBuilder.getCarsRelations(
                blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER1"),
                blockedCar = car
            ),
            TestObjectBuilder.getCarsRelations(
                blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKER2"),
                blockedCar = car
            )
        )

        every { carsRelationsRepository.findByBlockingCar(car) } returns blockingRelations
        every { carsRelationsRepository.findByBlockedCar(car) } returns blockedByRelations
        every { carsRelationsRepository.delete(any()) } just Runs

        // When
        carsRelationsService.deleteAllCarsRelations(car)

        // Then
        verify {
            carsRelationsRepository.findByBlockingCar(car)
            carsRelationsRepository.findByBlockedCar(car)
            carsRelationsRepository.delete(blockingRelations[0])
            carsRelationsRepository.delete(blockingRelations[1])
            carsRelationsRepository.delete(blockedByRelations[0])
            carsRelationsRepository.delete(blockedByRelations[1])
        }
    }

    @Test
    fun `test wouldCreateCircularBlocking direct circular`() {
        // Given
        val car1 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car2 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val existingRelation = TestObjectBuilder.getCarsRelations(blockingCar = car2, blockedCar = car1)

        every { car1.id } returns 1L
        every { car2.id } returns 2L
        every { car1.plateNumber } returns "CAR1"
        every { car2.plateNumber } returns "CAR2"
        every { carsRelationsRepository.findByBlockingCar(car1) } returns emptyList()
        every { carsRelationsRepository.findByBlockingCar(car2) } returns listOf(existingRelation)

        // When
        val result = carsRelationsService.wouldCreateCircularBlocking(car1, car2)

        // Then
        assertTrue(result)
        verify { carsRelationsRepository.findByBlockingCar(any()) }
    }

    @Test
    fun `test wouldCreateCircularBlocking indirect circular`() {
        // Given
        val car1 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car2 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car3 = mockk<com.yb.rh.entities.Car>(relaxed = true)

        val relation2to3 = TestObjectBuilder.getCarsRelations(blockingCar = car2, blockedCar = car3)
        val relation3to1 = TestObjectBuilder.getCarsRelations(blockingCar = car3, blockedCar = car1)

        every { car1.id } returns 1L
        every { car2.id } returns 2L
        every { car3.id } returns 3L
        every { car1.plateNumber } returns "CAR1"
        every { car2.plateNumber } returns "CAR2"
        every { car3.plateNumber } returns "CAR3"
        every { carsRelationsRepository.findByBlockingCar(car1) } returns emptyList()
        every { carsRelationsRepository.findByBlockingCar(car2) } returns listOf(relation2to3)
        every { carsRelationsRepository.findByBlockingCar(car3) } returns listOf(relation3to1)

        // When
        val result = carsRelationsService.wouldCreateCircularBlocking(car1, car2)

        // Then
        assertTrue(result)
        verify {
            carsRelationsRepository.findByBlockingCar(car2)
            carsRelationsRepository.findByBlockingCar(car3)
        }
    }

    @Test
    fun `test wouldCreateCircularBlocking no circular`() {
        // Given
        val car1 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car2 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car3 = mockk<com.yb.rh.entities.Car>(relaxed = true)

        val relation2to3 = TestObjectBuilder.getCarsRelations(blockingCar = car2, blockedCar = car3)

        every { car1.id } returns 1L
        every { car2.id } returns 2L
        every { car3.id } returns 3L
        every { car1.plateNumber } returns "CAR1"
        every { car2.plateNumber } returns "CAR2"
        every { car3.plateNumber } returns "CAR3"
        every { carsRelationsRepository.findByBlockingCar(car1) } returns emptyList()
        every { carsRelationsRepository.findByBlockingCar(car2) } returns listOf(relation2to3)
        every { carsRelationsRepository.findByBlockingCar(car3) } returns emptyList()

        // When
        val result = carsRelationsService.wouldCreateCircularBlocking(car1, car2)

        // Then
        assertFalse(result)
        verify {
            carsRelationsRepository.findByBlockingCar(car2)
            carsRelationsRepository.findByBlockingCar(car3)
        }
    }

    @Test
    fun `test wouldCreateCircularBlocking self blocking`() {
        // Given
        val car = TestObjectBuilder.getCar(id = 1L, plateNumber = "CAR1")

        every { carsRelationsRepository.findByBlockingCar(car) } returns emptyList()

        // When
        val result = carsRelationsService.wouldCreateCircularBlocking(car, car)

        // Then
        assertTrue(result)
        verify(exactly = 0) { carsRelationsRepository.findByBlockingCar(any()) }
    }

    @Test
    fun `test hasPathToCar direct path`() {
        val car1 = TestObjectBuilder.getCar(id = 1L)
        val car2 = TestObjectBuilder.getCar(id = 2L)
        val visited = mutableSetOf<Long>()

        val result = carsRelationsService.hasPathToCar(car1, car1, visited)

        assertTrue(result)
    }

    @Test
    fun `test hasPathToCar no path`() {
        val car1 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val car2 = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val visited = mutableSetOf<Long>()

        every { car1.id } returns 1L
        every { car2.id } returns 2L
        every { carsRelationsRepository.findByBlockingCar(car1) } returns emptyList()

        val result = carsRelationsService.hasPathToCar(car1, car2, visited)

        assertFalse(result)
    }

    @Test
    fun `test hasPathToCar with circular reference`() {
        val car1 = TestObjectBuilder.getCar(id = 1L)
        val car2 = TestObjectBuilder.getCar(id = 2L)
        val visited = mutableSetOf<Long>()

        val relation1 = mockk<com.yb.rh.entities.CarsRelations>()
        val relation2 = mockk<com.yb.rh.entities.CarsRelations>()

        every { relation1.blockedCar } returns car2
        every { relation2.blockedCar } returns car1
        every { carsRelationsRepository.findByBlockingCar(car1) } returns listOf(relation1)
        every { carsRelationsRepository.findByBlockingCar(car2) } returns listOf(relation2)

        val result = carsRelationsService.hasPathToCar(car1, car2, visited)

        assertTrue(result)
    }
} 