package com.yb.rh.entities

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.UserCreationDTO
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserEntityTest {

    @Test
    fun `test User constructor with all parameters`() {
        // Given
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val pushNotificationToken = "token123"
        val urlPhoto = "http://example.com/photo.jpg"
        val isActive = true
        val creationTime = LocalDateTime.now()
        val updateTime = LocalDateTime.now()
        val userId = 1L

        // When
        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pushNotificationToken = pushNotificationToken,
            urlPhoto = urlPhoto,
            isActive = isActive,
            creationTime = creationTime,
            updateTime = updateTime,
            userId = userId
        )

        // Then
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertEquals(email, user.email)
        assertEquals(pushNotificationToken, user.pushNotificationToken)
        assertEquals(urlPhoto, user.urlPhoto)
        assertEquals(isActive, user.isActive)
        assertEquals(creationTime, user.creationTime)
        assertEquals(updateTime, user.updateTime)
        assertEquals(userId, user.userId)
    }

    @Test
    fun `test User constructor with default parameters`() {
        // Given
        val firstName = "Jane"
        val lastName = "Smith"
        val email = "jane.smith@example.com"
        val pushNotificationToken = "token456"
        val urlPhoto = "http://example.com/jane.jpg"
        val isActive = false
        val creationTime = LocalDateTime.now()
        val updateTime = LocalDateTime.now()
        val userId = 2L

        // When
        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pushNotificationToken = pushNotificationToken,
            urlPhoto = urlPhoto,
            isActive = isActive,
            creationTime = creationTime,
            updateTime = updateTime,
            userId = userId
        )

        // Then
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertEquals(email, user.email)
        assertEquals(pushNotificationToken, user.pushNotificationToken)
        assertEquals(urlPhoto, user.urlPhoto)
        assertEquals(isActive, user.isActive)
        assertEquals(creationTime, user.creationTime)
        assertEquals(updateTime, user.updateTime)
        assertEquals(userId, user.userId)
    }

    @Test
    fun `test User toDto extension function`() {
        // Given
        val user = TestObjectBuilder.getUser()

        // When
        val userDTO = user.toDto()

        // Then
        assertNotNull(userDTO)
        assertEquals(user.userId, userDTO.id)
        assertEquals(user.firstName, userDTO.firstName)
        assertEquals(user.lastName, userDTO.lastName)
        assertEquals(user.email, userDTO.email)
        assertEquals(user.urlPhoto, userDTO.urlPhoto)
    }

    @Test
    fun `test User fromDto companion function`() {
        // Given
        val userCreationDTO = TestObjectBuilder.getUserCreationDTO()

        // When
        val user = User.fromDto(userCreationDTO)

        // Then
        assertNotNull(user)
        assertEquals(userCreationDTO.firstName, user.firstName)
        assertEquals(userCreationDTO.lastName, user.lastName)
        assertEquals(userCreationDTO.email, user.email)
        assertEquals(userCreationDTO.pushNotificationToken, user.pushNotificationToken)
        assertEquals(userCreationDTO.urlPhoto, user.urlPhoto)
        assertEquals(true, user.isActive)
        assertNotNull(user.creationTime)
        assertNotNull(user.updateTime)
        assertEquals(0L, user.userId)
    }

    @Test
    fun `test User fromDto with null urlPhoto`() {
        // Given
        val userCreationDTO = UserCreationDTO(
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            pushNotificationToken = "token123",
            urlPhoto = null
        )

        // When
        val user = User.fromDto(userCreationDTO)

        // Then
        assertNotNull(user)
        assertEquals("Test", user.firstName)
        assertEquals("User", user.lastName)
        assertEquals("test@example.com", user.email)
        assertEquals("token123", user.pushNotificationToken)
        assertEquals(null, user.urlPhoto)
    }

    @Test
    fun `test User property setters`() {
        // Given
        val user = TestObjectBuilder.getUser()

        // When
        user.firstName = "Updated"
        user.lastName = "Name"
        user.email = "updated@example.com"
        user.pushNotificationToken = "newtoken"
        user.urlPhoto = "http://example.com/updated.jpg"
        user.isActive = false
        user.creationTime = LocalDateTime.of(2023, 1, 1, 0, 0)
        user.updateTime = LocalDateTime.of(2023, 12, 31, 23, 59)

        // Then
        assertEquals("Updated", user.firstName)
        assertEquals("Name", user.lastName)
        assertEquals("updated@example.com", user.email)
        assertEquals("newtoken", user.pushNotificationToken)
        assertEquals("http://example.com/updated.jpg", user.urlPhoto)
        assertEquals(false, user.isActive)
        assertEquals(LocalDateTime.of(2023, 1, 1, 0, 0), user.creationTime)
        assertEquals(LocalDateTime.of(2023, 12, 31, 23, 59), user.updateTime)
    }

    @Test
    fun `test User copy function`() {
        // Given
        val originalUser = TestObjectBuilder.getUser()

        // When
        val copiedUser = originalUser.copy(
            firstName = "Copied",
            lastName = "User",
            email = "copied@example.com"
        )

        // Then
        assertEquals("Copied", copiedUser.firstName)
        assertEquals("User", copiedUser.lastName)
        assertEquals("copied@example.com", copiedUser.email)
        assertEquals(originalUser.pushNotificationToken, copiedUser.pushNotificationToken)
        assertEquals(originalUser.urlPhoto, copiedUser.urlPhoto)
        assertEquals(originalUser.isActive, copiedUser.isActive)
        assertEquals(originalUser.creationTime, copiedUser.creationTime)
        assertEquals(originalUser.updateTime, copiedUser.updateTime)
        assertEquals(originalUser.userId, copiedUser.userId)
    }

    @Test
    fun `test User equals and hashCode`() {
        // Given
        val user1 = TestObjectBuilder.getUser()
        val user2 = TestObjectBuilder.getUser()
        val user3 = TestObjectBuilder.getUser(userId = 999L)

        // When & Then
        assertEquals(user1, user1) // Same object
        assertEquals(user1.hashCode(), user1.hashCode())
        assert(user1 != user2) // Different objects with same values
        assert(user1 != user3) // Different objects with different values
    }

    @Test
    fun `test User toString`() {
        // Given
        val user = TestObjectBuilder.getUser()

        // When
        val toString = user.toString()

        // Then
        assertNotNull(toString)
        assert(toString.contains(user.firstName))
        assert(toString.contains(user.lastName))
        assert(toString.contains(user.email))
    }
} 