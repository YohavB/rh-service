package com.yb.rh.repositories

import com.yb.rh.TestObjectBuilder
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsRelations
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@ActiveProfiles("test")
class CarsRelationsRepositoryTest {

    @Autowired
    private lateinit var carsRelationsRepository: CarsRelationsRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var blockingCar: Car
    private lateinit var blockedCar: Car
    private lateinit var testCarsRelation: CarsRelations

    @BeforeEach
    fun setUp() {
        blockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKING123")
        blockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED123")
        entityManager.persistAndFlush(blockingCar)
        entityManager.persistAndFlush(blockedCar)
        
        testCarsRelation = TestObjectBuilder.getCarsRelations(blockingCar = blockingCar, blockedCar = blockedCar)
        entityManager.persistAndFlush(testCarsRelation)
    }

    @Test
    fun `test findByBlockingCar with existing relations`() {
        // Given
        val anotherBlockedCar = TestObjectBuilder.getCar(plateNumber = "BLOCKED456")
        entityManager.persistAndFlush(anotherBlockedCar)
        val anotherRelation = TestObjectBuilder.getCarsRelations(blockingCar = blockingCar, blockedCar = anotherBlockedCar)
        entityManager.persistAndFlush(anotherRelation)

        // When
        val blockingRelations = carsRelationsRepository.findByBlockingCar(blockingCar)

        // Then
        assertNotNull(blockingRelations)
        assertEquals(2, blockingRelations.size)
        assertTrue(blockingRelations.all { it.blockingCar.id == blockingCar.id })
    }

    @Test
    fun `test findByBlockingCar with no relations`() {
        // Given
        val otherCar = TestObjectBuilder.getCar(plateNumber = "OTHER123")
        entityManager.persistAndFlush(otherCar)

        // When
        val blockingRelations = carsRelationsRepository.findByBlockingCar(otherCar)

        // Then
        assertNotNull(blockingRelations)
        assertEquals(0, blockingRelations.size)
    }

    @Test
    fun `test findByBlockedCar with existing relations`() {
        // Given
        val anotherBlockingCar = TestObjectBuilder.getCar(plateNumber = "BLOCKING456")
        entityManager.persistAndFlush(anotherBlockingCar)
        val anotherRelation = TestObjectBuilder.getCarsRelations(blockingCar = anotherBlockingCar, blockedCar = blockedCar)
        entityManager.persistAndFlush(anotherRelation)

        // When
        val blockedRelations = carsRelationsRepository.findByBlockedCar(blockedCar)

        // Then
        assertNotNull(blockedRelations)
        assertEquals(2, blockedRelations.size)
        assertTrue(blockedRelations.all { it.blockedCar.id == blockedCar.id })
    }

    @Test
    fun `test findByBlockedCar with no relations`() {
        // Given
        val otherCar = TestObjectBuilder.getCar(plateNumber = "OTHER123")
        entityManager.persistAndFlush(otherCar)

        // When
        val blockedRelations = carsRelationsRepository.findByBlockedCar(otherCar)

        // Then
        assertNotNull(blockedRelations)
        assertEquals(0, blockedRelations.size)
    }

    @Test
    fun `test save new cars relation`() {
        // Given
        val newBlockingCar = TestObjectBuilder.getCar(plateNumber = "NEWBLOCKING123")
        val newBlockedCar = TestObjectBuilder.getCar(plateNumber = "NEWBLOCKED123")
        entityManager.persistAndFlush(newBlockingCar)
        entityManager.persistAndFlush(newBlockedCar)
        val newRelation = TestObjectBuilder.getCarsRelations(blockingCar = newBlockingCar, blockedCar = newBlockedCar)

        // When
        val savedRelation = carsRelationsRepository.save(newRelation)

        // Then
        assertNotNull(savedRelation)
        assertNotNull(savedRelation.id)
        assertEquals(newBlockingCar.id, savedRelation.blockingCar.id)
        assertEquals(newBlockedCar.id, savedRelation.blockedCar.id)
    }

    @Test
    fun `test findById with existing cars relation`() {
        // When
        val foundRelation = carsRelationsRepository.findById(testCarsRelation.id)

        // Then
        assertNotNull(foundRelation)
        assertTrue(foundRelation.isPresent)
        assertEquals(testCarsRelation.id, foundRelation.get().id)
    }

    @Test
    fun `test findById with non-existing cars relation`() {
        // When
        val foundRelation = carsRelationsRepository.findById(999L)

        // Then
        assertNotNull(foundRelation)
        assertFalse(foundRelation.isPresent)
    }

    @Test
    fun `test findAll cars relations`() {
        // Given
        val car1 = TestObjectBuilder.getCar(plateNumber = "CAR1")
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR2")
        entityManager.persistAndFlush(car1)
        entityManager.persistAndFlush(car2)
        val relation2 = TestObjectBuilder.getCarsRelations(blockingCar = car1, blockedCar = car2)
        entityManager.persistAndFlush(relation2)

        // When
        val allRelations = carsRelationsRepository.findAll()

        // Then
        assertNotNull(allRelations)
        assertEquals(2, allRelations.count())
    }

    @Test
    fun `test delete cars relation`() {
        // When
        carsRelationsRepository.delete(testCarsRelation)

        // Then
        val foundRelation = carsRelationsRepository.findById(testCarsRelation.id)
        assertFalse(foundRelation.isPresent)
    }

    @Test
    fun `test count cars relations`() {
        // Given
        val car1 = TestObjectBuilder.getCar(plateNumber = "CAR1")
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR2")
        entityManager.persistAndFlush(car1)
        entityManager.persistAndFlush(car2)
        val relation2 = TestObjectBuilder.getCarsRelations(blockingCar = car1, blockedCar = car2)
        entityManager.persistAndFlush(relation2)

        // When
        val count = carsRelationsRepository.count()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `test existsById with existing cars relation`() {
        // When
        val exists = carsRelationsRepository.existsById(testCarsRelation.id)

        // Then
        assertTrue(exists)
    }

    @Test
    fun `test existsById with non-existing cars relation`() {
        // When
        val exists = carsRelationsRepository.existsById(999L)

        // Then
        assertFalse(exists)
    }
} 