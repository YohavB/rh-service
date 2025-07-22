package com.yb.rh.dtos

import com.yb.rh.TestObjectBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserDTOTest {

    @Test
    fun `test UserDTO constructor with all parameters`() {
        val id = 1L
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val urlPhoto = "http://example.com/photo.jpg"

        val userDTO = UserDTO(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            urlPhoto = urlPhoto
        )

        assertEquals(id, userDTO.id)
        assertEquals(firstName, userDTO.firstName)
        assertEquals(lastName, userDTO.lastName)
        assertEquals(email, userDTO.email)
        assertEquals(urlPhoto, userDTO.urlPhoto)
    }

    @Test
    fun `test UserDTO constructor with null urlPhoto`() {
        val id = 2L
        val firstName = "Jane"
        val lastName = "Smith"
        val email = "jane.smith@example.com"
        val urlPhoto: String? = null

        val userDTO = UserDTO(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            urlPhoto = urlPhoto
        )

        assertEquals(id, userDTO.id)
        assertEquals(firstName, userDTO.firstName)
        assertEquals(lastName, userDTO.lastName)
        assertEquals(email, userDTO.email)
        assertEquals(null, userDTO.urlPhoto)
    }

    @Test
    fun `test UserCreationDTO constructor with all parameters`() {
        val firstName = "New"
        val lastName = "User"
        val email = "new.user@example.com"
        val pushNotificationToken = "token123"
        val urlPhoto = "http://example.com/new.jpg"

        val userCreationDTO = UserCreationDTO(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pushNotificationToken = pushNotificationToken,
            urlPhoto = urlPhoto
        )

        assertEquals(firstName, userCreationDTO.firstName)
        assertEquals(lastName, userCreationDTO.lastName)
        assertEquals(email, userCreationDTO.email)
        assertEquals(pushNotificationToken, userCreationDTO.pushNotificationToken)
        assertEquals(urlPhoto, userCreationDTO.urlPhoto)
    }

    @Test
    fun `test UserCreationDTO constructor with null urlPhoto`() {
        val firstName = "Test"
        val lastName = "User"
        val email = "test.user@example.com"
        val pushNotificationToken = "token456"
        val urlPhoto: String? = null

        val userCreationDTO = UserCreationDTO(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pushNotificationToken = pushNotificationToken,
            urlPhoto = urlPhoto
        )

        assertEquals(firstName, userCreationDTO.firstName)
        assertEquals(lastName, userCreationDTO.lastName)
        assertEquals(email, userCreationDTO.email)
        assertEquals(pushNotificationToken, userCreationDTO.pushNotificationToken)
        assertEquals(null, userCreationDTO.urlPhoto)
    }

    @Test
    fun `test UserDTO copy function`() {
        val originalUserDTO = TestObjectBuilder.getUserDTO()

        val copiedUserDTO = originalUserDTO.copy(
            firstName = "Copied",
            lastName = "User",
            email = "copied@example.com"
        )

        assertEquals("Copied", copiedUserDTO.firstName)
        assertEquals("User", copiedUserDTO.lastName)
        assertEquals("copied@example.com", copiedUserDTO.email)
        assertEquals(originalUserDTO.id, copiedUserDTO.id)
        assertEquals(originalUserDTO.urlPhoto, copiedUserDTO.urlPhoto)
    }

    @Test
    fun `test UserCreationDTO copy function`() {
        val originalUserCreationDTO = TestObjectBuilder.getUserCreationDTO()

        val copiedUserCreationDTO = originalUserCreationDTO.copy(
            firstName = "Copied",
            lastName = "User",
            email = "copied@example.com"
        )

        assertEquals("Copied", copiedUserCreationDTO.firstName)
        assertEquals("User", copiedUserCreationDTO.lastName)
        assertEquals("copied@example.com", copiedUserCreationDTO.email)
        assertEquals(originalUserCreationDTO.pushNotificationToken, copiedUserCreationDTO.pushNotificationToken)
        assertEquals(originalUserCreationDTO.urlPhoto, copiedUserCreationDTO.urlPhoto)
    }

    @Test
    fun `test UserDTO equals and hashCode`() {
        val userDTO1 = TestObjectBuilder.getUserDTO()
        val userDTO2 = TestObjectBuilder.getUserDTO()
        val userDTO3 = TestObjectBuilder.getUserDTO(id = 999L)

        assertEquals(userDTO1, userDTO1) // Same object
        assertEquals(userDTO1.hashCode(), userDTO1.hashCode())
        assertEquals(userDTO1, userDTO2) // Different objects with same values should be equal (data class behavior)
        assertEquals(userDTO1.hashCode(), userDTO2.hashCode())
        assert(userDTO1 != userDTO3) // Different objects with different values
    }

    @Test
    fun `test UserCreationDTO equals and hashCode`() {
        val userCreationDTO1 = TestObjectBuilder.getUserCreationDTO()
        val userCreationDTO2 = TestObjectBuilder.getUserCreationDTO()
        val userCreationDTO3 = TestObjectBuilder.getUserCreationDTO(email = "different@example.com")

        assertEquals(userCreationDTO1, userCreationDTO1) // Same object
        assertEquals(userCreationDTO1.hashCode(), userCreationDTO1.hashCode())
        assertEquals(userCreationDTO1, userCreationDTO2) // Different objects with same values should be equal (data class behavior)
        assertEquals(userCreationDTO1.hashCode(), userCreationDTO2.hashCode())
        assert(userCreationDTO1 != userCreationDTO3) // Different objects with different values
    }

    @Test
    fun `test UserDTO toString`() {
        val userDTO = TestObjectBuilder.getUserDTO()

        val toString = userDTO.toString()

        assertNotNull(toString)
        assert(toString.contains(userDTO.firstName))
        assert(toString.contains(userDTO.lastName))
        assert(toString.contains(userDTO.email))
    }

    @Test
    fun `test UserCreationDTO toString`() {
        val userCreationDTO = TestObjectBuilder.getUserCreationDTO()

        val toString = userCreationDTO.toString()

        assertNotNull(toString)
        assert(toString.contains(userCreationDTO.firstName))
        assert(toString.contains(userCreationDTO.lastName))
        assert(toString.contains(userCreationDTO.email))
    }

    @Test
    fun `test UserDTO with different email formats`() {
        val userDTO1 = TestObjectBuilder.getUserDTO(email = "user1@example.com")
        val userDTO2 = TestObjectBuilder.getUserDTO(email = "user2@test.org")
        val userDTO3 = TestObjectBuilder.getUserDTO(email = "user3@company.co.uk")

        assertEquals("user1@example.com", userDTO1.email)
        assertEquals("user2@test.org", userDTO2.email)
        assertEquals("user3@company.co.uk", userDTO3.email)
    }

    @Test
    fun `test UserCreationDTO with different push notification tokens`() {
        val userCreationDTO1 = TestObjectBuilder.getUserCreationDTO(pushNotificationToken = "token123")
        val userCreationDTO2 = TestObjectBuilder.getUserCreationDTO(pushNotificationToken = "expo-token-456")
        val userCreationDTO3 = TestObjectBuilder.getUserCreationDTO(pushNotificationToken = "fcm-token-789")

        assertEquals("token123", userCreationDTO1.pushNotificationToken)
        assertEquals("expo-token-456", userCreationDTO2.pushNotificationToken)
        assertEquals("fcm-token-789", userCreationDTO3.pushNotificationToken)
    }

    @Test
    fun `test UserDTO with different URL photo formats`() {
        val userDTO1 = TestObjectBuilder.getUserDTO(urlPhoto = "http://example.com/photo1.jpg")
        val userDTO2 = TestObjectBuilder.getUserDTO(urlPhoto = "https://cdn.example.com/photo2.png")
        val userDTO3 = TestObjectBuilder.getUserDTO(urlPhoto = null)

        assertEquals("http://example.com/photo1.jpg", userDTO1.urlPhoto)
        assertEquals("https://cdn.example.com/photo2.png", userDTO2.urlPhoto)
        assertEquals(null, userDTO3.urlPhoto)
    }
} 