package com.yb.rh.repositories

import com.yb.rh.TestObjectBuilder
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UserCar
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.test.*

@DataJpaTest
@ActiveProfiles("test")
class UserCarRepositoryTest {

    @Autowired
    private lateinit var userCarRepository: UserCarRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private lateinit var testUser: User
    private lateinit var testCar: Car
    private lateinit var testUserCar: UserCar

    @BeforeEach
    fun setUp() {
        testUser = TestObjectBuilder.getUser()
        testCar = TestObjectBuilder.getCar()
        entityManager.persistAndFlush(testUser)
        entityManager.persistAndFlush(testCar)
        
        testUserCar = TestObjectBuilder.getUserCar(user = testUser, car = testCar)
        entityManager.persistAndFlush(testUserCar)
    }

    @Test
    fun `test findByUserAndCar with existing relation`() {
        // When
        val foundUserCar = userCarRepository.findByUserAndCar(testUser, testCar)

        // Then
        assertNotNull(foundUserCar)
        assertEquals(testUser.userId, foundUserCar.user.userId)
        assertEquals(testCar.id, foundUserCar.car.id)
    }

    @Test
    fun `test findByUserAndCar with non-existing relation`() {
        // Given
        val otherUser = TestObjectBuilder.getUser(
            email = "other.user@example.com",
            pushNotificationToken = "ExponentPushToken[other-token-456]"
        )
        val otherCar = TestObjectBuilder.getCar(plateNumber = "XYZ789")
        entityManager.persistAndFlush(otherUser)
        entityManager.persistAndFlush(otherCar)

        // When
        val foundUserCar = userCarRepository.findByUserAndCar(otherUser, otherCar)

        // Then
        assertNull(foundUserCar)
    }

    @Test
    fun `test findAllByUser with existing relations`() {
        // Given
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR456")
        entityManager.persistAndFlush(car2)
        val userCar2 = TestObjectBuilder.getUserCar(user = testUser, car = car2)
        entityManager.persistAndFlush(userCar2)

        // When
        val userCars = userCarRepository.findAllByUser(testUser)

        // Then
        assertNotNull(userCars)
        assertEquals(2, userCars.size)
        assertTrue(userCars.all { it.user.userId == testUser.userId })
    }

    @Test
    fun `test findAllByUser with no relations`() {
        // Given
        val otherUser = TestObjectBuilder.getUser(
            email = "other.user@example.com",
            pushNotificationToken = "ExponentPushToken[other-token-456]"
        )
        entityManager.persistAndFlush(otherUser)

        // When
        val userCars = userCarRepository.findAllByUser(otherUser)

        // Then
        assertNotNull(userCars)
        assertEquals(0, userCars.size)
    }

    @Test
    fun `test findAllByCar with existing relations`() {
        // Given
        val user2 = TestObjectBuilder.getUser(
            email = "user2@example.com",
            pushNotificationToken = "ExponentPushToken[user2-token-789]"
        )
        entityManager.persistAndFlush(user2)
        val userCar2 = TestObjectBuilder.getUserCar(user = user2, car = testCar)
        entityManager.persistAndFlush(userCar2)

        // When
        val carUsers = userCarRepository.findAllByCar(testCar)

        // Then
        assertNotNull(carUsers)
        assertEquals(2, carUsers.size)
        assertTrue(carUsers.all { it.car.id == testCar.id })
    }

    @Test
    fun `test findAllByCar with no relations`() {
        // Given
        val otherCar = TestObjectBuilder.getCar(plateNumber = "OTHER123")
        entityManager.persistAndFlush(otherCar)

        // When
        val carUsers = userCarRepository.findAllByCar(otherCar)

        // Then
        assertNotNull(carUsers)
        assertEquals(0, carUsers.size)
    }

    @Test
    fun `test save new user car relation`() {
        // Given
        val newUser = TestObjectBuilder.getUser(
            email = "new.user@example.com",
            pushNotificationToken = "ExponentPushToken[new-token-101]"
        )
        val newCar = TestObjectBuilder.getCar(plateNumber = "NEW123")
        entityManager.persistAndFlush(newUser)
        entityManager.persistAndFlush(newCar)
        val newUserCar = TestObjectBuilder.getUserCar(user = newUser, car = newCar)

        // When
        val savedUserCar = userCarRepository.save(newUserCar)

        // Then
        assertNotNull(savedUserCar)
        assertNotNull(savedUserCar.id)
        assertEquals(newUser.userId, savedUserCar.user.userId)
        assertEquals(newCar.id, savedUserCar.car.id)
    }

    @Test
    fun `test findById with existing user car`() {
        // When
        val foundUserCar = userCarRepository.findById(testUserCar.id)

        // Then
        assertNotNull(foundUserCar)
        assertTrue(foundUserCar.isPresent)
        assertEquals(testUserCar.id, foundUserCar.get().id)
    }

    @Test
    fun `test findById with non-existing user car`() {
        // When
        val foundUserCar = userCarRepository.findById(999L)

        // Then
        assertNotNull(foundUserCar)
        assertFalse(foundUserCar.isPresent)
    }

    @Test
    fun `test findAll user cars`() {
        // Given
        val user2 = TestObjectBuilder.getUser(
            email = "user2@example.com",
            pushNotificationToken = "ExponentPushToken[user2-token-789]"
        )
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR456")
        entityManager.persistAndFlush(user2)
        entityManager.persistAndFlush(car2)
        val userCar2 = TestObjectBuilder.getUserCar(user = user2, car = car2)
        entityManager.persistAndFlush(userCar2)

        // When
        val allUserCars = userCarRepository.findAll()

        // Then
        assertNotNull(allUserCars)
        assertEquals(2, allUserCars.count())
    }

    @Test
    fun `test delete user car`() {
        // When
        userCarRepository.delete(testUserCar)

        // Then
        val foundUserCar = userCarRepository.findById(testUserCar.id)
        assertFalse(foundUserCar.isPresent)
    }

    @Test
    fun `test count user cars`() {
        // Given
        val user2 = TestObjectBuilder.getUser(
            email = "user2@example.com",
            pushNotificationToken = "ExponentPushToken[user2-token-789]"
        )
        val car2 = TestObjectBuilder.getCar(plateNumber = "CAR456")
        entityManager.persistAndFlush(user2)
        entityManager.persistAndFlush(car2)
        val userCar2 = TestObjectBuilder.getUserCar(user = user2, car = car2)
        entityManager.persistAndFlush(userCar2)

        // When
        val count = userCarRepository.count()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `test existsById with existing user car`() {
        // When
        val exists = userCarRepository.existsById(testUserCar.id)

        // Then
        assertTrue(exists)
    }

    @Test
    fun `test existsById with non-existing user car`() {
        // When
        val exists = userCarRepository.existsById(999L)

        // Then
        assertFalse(exists)
    }
} 