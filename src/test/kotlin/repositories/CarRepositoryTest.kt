package com.yb.rh.repositories

import com.yb.rh.TestObjectBuilder
import com.yb.rh.entities.Car
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.test.*

@DataJpaTest
@ActiveProfiles("test")
class CarRepositoryTest {

    @Autowired
    private lateinit var carRepository: CarRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var testCar: Car

    @BeforeEach
    fun setUp() {
        testCar = TestObjectBuilder.getCar()
        entityManager.persistAndFlush(testCar)
    }

    @Test
    fun `test findByPlateNumber with existing car`() {
        // When
        val foundCar = carRepository.findByPlateNumber(testCar.plateNumber)

        // Then
        assertNotNull(foundCar)
        assertEquals(testCar.plateNumber, foundCar.plateNumber)
        assertEquals(testCar.country, foundCar.country)
        assertEquals(testCar.brand, foundCar.brand)
        assertEquals(testCar.model, foundCar.model)
    }

    @Test
    fun `test findByPlateNumber with non-existing car`() {
        // When
        val foundCar = carRepository.findByPlateNumber("NONEXISTENT")

        // Then
        assertNull(foundCar)
    }

    @Test
    fun `test findCarById with existing car`() {
        // When
        val foundCar = carRepository.findCarById(testCar.id)

        // Then
        assertNotNull(foundCar)
        assertEquals(testCar.id, foundCar.id)
        assertEquals(testCar.plateNumber, foundCar.plateNumber)
    }

    @Test
    fun `test findCarById with non-existing car`() {
        // When
        val foundCar = carRepository.findCarById(999L)

        // Then
        assertNull(foundCar)
    }

    @Test
    fun `test save new car`() {
        // Given
        val newCar = TestObjectBuilder.getCar(plateNumber = "NEW123")

        // When
        val savedCar = carRepository.save(newCar)

        // Then
        assertNotNull(savedCar)
        assertNotNull(savedCar.id)
        assertEquals("NEW123", savedCar.plateNumber)
    }

    @Test
    fun `test findById with existing car`() {
        // When
        val foundCar = carRepository.findById(testCar.id)

        // Then
        assertNotNull(foundCar)
        assertTrue(foundCar.isPresent)
        assertEquals(testCar.id, foundCar.get().id)
    }

    @Test
    fun `test findById with non-existing car`() {
        // When
        val foundCar = carRepository.findById(999L)

        // Then
        assertNotNull(foundCar)
        assertFalse(foundCar.isPresent)
    }

    @Test
    fun `test findAll cars`() {
        // Given
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR456")
        entityManager.persistAndFlush(car2)

        // When
        val allCars = carRepository.findAll()

        // Then
        assertNotNull(allCars)
        assertEquals(2, allCars.count())
    }

    @Test
    fun `test delete car`() {
        // When
        carRepository.delete(testCar)

        // Then
        val foundCar = carRepository.findById(testCar.id)
        assertFalse(foundCar.isPresent)
    }

    @Test
    fun `test count cars`() {
        // Given
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR456")
        entityManager.persistAndFlush(car2)

        // When
        val count = carRepository.count()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `test existsById with existing car`() {
        // When
        val exists = carRepository.existsById(testCar.id)

        // Then
        assertTrue(exists)
    }

    @Test
    fun `test existsById with non-existing car`() {
        // When
        val exists = carRepository.existsById(999L)

        // Then
        assertFalse(exists)
    }
} 