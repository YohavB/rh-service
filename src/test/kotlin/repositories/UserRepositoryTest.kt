package com.yb.rh.repositories

import com.yb.rh.TestObjectBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import kotlin.test.*

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `test findByUserId with existing user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findByUserId(testUser.userId)

        // Then
        assertNotNull(foundUser)
        assertEquals(testUser.userId, foundUser.userId)
        assertEquals(testUser.email, foundUser.email)
    }

    @Test
    fun `test findByUserId with non-existing user`() {
        // When
        val foundUser = userRepository.findByUserId(999L)

        // Then
        assertNull(foundUser)
    }

    @Test
    fun `test findByEmail with existing user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findByEmail(testUser.email)

        // Then
        assertNotNull(foundUser)
        assertEquals(testUser.email, foundUser.email)
        assertEquals(testUser.userId, foundUser.userId)
    }

    @Test
    fun `test findByEmail with non-existing user`() {
        // When
        val foundUser = userRepository.findByEmail("nonexistent@example.com")

        // Then
        assertNull(foundUser)
    }

    @Test
    fun `test save new user`() {
        // Given
        val newUser = TestObjectBuilder.getUser(email = "new@example.com")

        // When
        val savedUser = userRepository.save(newUser)

        // Then
        assertNotNull(savedUser)
        assertNotNull(savedUser.userId)
        assertEquals("new@example.com", savedUser.email)
    }

    @Test
    fun `test save existing user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)
        testUser.firstName = "Updated"

        // When
        val savedUser = userRepository.save(testUser)

        // Then
        assertNotNull(savedUser)
        assertEquals("Updated", savedUser.firstName)
    }

    @Test
    fun `test findById with existing user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)

        // When
        val foundUser = userRepository.findById(testUser.userId)

        // Then
        assertNotNull(foundUser)
        assertTrue(foundUser.isPresent)
        assertEquals(testUser.userId, foundUser.get().userId)
    }

    @Test
    fun `test findById with non-existing user`() {
        // When
        val foundUser = userRepository.findById(999L)

        // Then
        assertNotNull(foundUser)
        assertFalse(foundUser.isPresent)
    }

    @Test
    fun `test findAll users`() {
        // Given
        val user1 = TestObjectBuilder.getUser(
            email = "user1@example.com",
            pushNotificationToken = "ExponentPushToken[user1-token-123]"
        )
        val user2 = TestObjectBuilder.getUser(
            email = "user2@example.com",
            pushNotificationToken = "ExponentPushToken[user2-token-456]"
        )
        entityManager.persistAndFlush(user1)
        entityManager.persistAndFlush(user2)

        // When
        val allUsers = userRepository.findAll()

        // Then
        assertNotNull(allUsers)
        assertEquals(2, allUsers.count())
    }

    @Test
    fun `test delete user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)

        // When
        userRepository.delete(testUser)

        // Then
        val foundUser = userRepository.findById(testUser.userId)
        assertFalse(foundUser.isPresent)
    }

    @Test
    fun `test count users`() {
        // Given
        val user1 = TestObjectBuilder.getUser(
            email = "user1@example.com",
            pushNotificationToken = "ExponentPushToken[user1-token-123]"
        )
        val user2 = TestObjectBuilder.getUser(
            email = "user2@example.com",
            pushNotificationToken = "ExponentPushToken[user2-token-456]"
        )
        entityManager.persistAndFlush(user1)
        entityManager.persistAndFlush(user2)

        // When
        val count = userRepository.count()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun `test existsById with existing user`() {
        // Given
        val testUser = TestObjectBuilder.getUser()
        entityManager.persistAndFlush(testUser)

        // When
        val exists = userRepository.existsById(testUser.userId)

        // Then
        assertTrue(exists)
    }

    @Test
    fun `test existsById with non-existing user`() {
        // When
        val exists = userRepository.existsById(999L)

        // Then
        assertFalse(exists)
    }
} 