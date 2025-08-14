package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.slot
import io.mockk.CapturingSlot
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import com.yb.rh.error.RHException
import com.yb.rh.error.ErrorType

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var currentUserService: CurrentUserService
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        currentUserService = mockk()
        userService = UserService(userRepository, currentUserService)
    }

    @Test
    fun `test getUserByEmail success`() {
        val email = "jane.doe@example.com"
        val user = TestObjectBuilder.getUser(email = email)

        every { userRepository.findByEmail(email) } returns user

        val result = userService.getUserByEmail(email)

        assertNotNull(result)
        assertEquals(email, result.email)
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test updateUser sets all fields from DTO including null photo`() {
        val existingUser = TestObjectBuilder.getUser(
            userId = 42L,
            firstName = "Old",
            lastName = "Name",
            email = "old.email@example.com",
            urlPhoto = "old.png",
            pushNotificationToken = "ExponentPushToken[old]",
            isActive = true
        )

        val userDTO = TestObjectBuilder.getUserDTO(
            id = 42L,
            firstName = "New",
            lastName = "Person",
            email = "new.email@example.com",
            urlPhoto = null
        )

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { userRepository.findByUserId(userDTO.id) } returns existingUser
        every { userRepository.save(capture(captured)) } answers { captured.captured }

        val result = userService.updateUser(userDTO)

        assertNotNull(result)
        assertEquals(userDTO.firstName, captured.captured.firstName)
        assertEquals(userDTO.lastName, captured.captured.lastName)
        assertEquals(userDTO.email, captured.captured.email)
        assertEquals(null, captured.captured.urlPhoto)
        // push token should remain the same on updateUser
        assertEquals("ExponentPushToken[old]", captured.captured.pushNotificationToken)

        verify { userRepository.findByUserId(userDTO.id) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test updatePushNotificationToken changes only the token`() {
        val originalUser = TestObjectBuilder.getUser(
            firstName = "First",
            lastName = "Last",
            email = "unchanged@example.com",
            pushNotificationToken = "ExponentPushToken[before]",
            urlPhoto = "pic.png",
            isActive = true
        )

        val newToken = "ExponentPushToken[after]"

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { currentUserService.getCurrentUser() } returns originalUser
        every { userRepository.save(capture(captured)) } answers { captured.captured }

        val result = userService.updatePushNotificationToken(newToken)

        assertNotNull(result)
        assertEquals(newToken, captured.captured.pushNotificationToken)
        assertEquals("First", captured.captured.firstName)
        assertEquals("Last", captured.captured.lastName)
        assertEquals("unchanged@example.com", captured.captured.email)
        assertEquals("pic.png", captured.captured.urlPhoto)
        assertTrue(captured.captured.isActive)

        verify { currentUserService.getCurrentUser() }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth keeps push token for existing user`() {
        val userDTO = TestObjectBuilder.getUserDTO(firstName = "Keep", lastName = "Token", urlPhoto = null)
        val existingPushToken = "ExponentPushToken[keep-this]"
        val existingUser = TestObjectBuilder.getUser(email = userDTO.email, pushNotificationToken = existingPushToken)

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { userRepository.findByEmail(userDTO.email) } returns existingUser
        every { userRepository.save(capture(captured)) } answers { captured.captured }

        val result = userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = true)

        assertNotNull(result)
        assertEquals(existingPushToken, captured.captured.pushNotificationToken)
        assertEquals(existingPushToken, result.pushNotificationToken)
        verify { userRepository.findByEmail(userDTO.email) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test getUserByEmail not found`() {
        val email = "missing.user@example.com"

        every { userRepository.findByEmail(email) } returns null

        assertThrows<RHException> {
            userService.getUserByEmail(email)
        }
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth updates existing user and preserves existing photo when dto photo is null`() {
        val existingPhoto = "existing-photo.png"
        val userDTO = TestObjectBuilder.getUserDTO(firstName = "Jane", lastName = "Roe", urlPhoto = null)
        val existingUser = TestObjectBuilder.getUser(email = userDTO.email, urlPhoto = existingPhoto)

        val savedUser = existingUser.copy(firstName = userDTO.firstName, lastName = userDTO.lastName, urlPhoto = existingPhoto)

        every { userRepository.findByEmail(userDTO.email) } returns existingUser

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { userRepository.save(capture(captured)) } answers {
            savedUser
        }

        val result = userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = true)

        assertNotNull(result)
        assertEquals(userDTO.email, result.email)
        assertEquals(userDTO.firstName, result.firstName)
        assertEquals(userDTO.lastName, result.lastName)
        assertEquals(existingPhoto, result.urlPhoto)

        // Verify the saved entity carried expected values
        assertTrue(captured.isCaptured)
        assertEquals(userDTO.firstName, captured.captured.firstName)
        assertEquals(userDTO.lastName, captured.captured.lastName)
        assertEquals(existingPhoto, captured.captured.urlPhoto)

        verify { userRepository.findByEmail(userDTO.email) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth updates existing user and overrides photo when dto photo provided`() {
        val newPhoto = "new-photo.png"
        val userDTO = TestObjectBuilder.getUserDTO(firstName = "Janet", lastName = "Smith", urlPhoto = newPhoto)
        val existingUser = TestObjectBuilder.getUser(email = userDTO.email, urlPhoto = "old.png")

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { userRepository.findByEmail(userDTO.email) } returns existingUser
        every { userRepository.save(capture(captured)) } answers { captured.captured }

        val result = userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = true)

        assertNotNull(result)
        assertEquals(newPhoto, result.urlPhoto)
        assertEquals(userDTO.firstName, result.firstName)
        assertEquals(userDTO.lastName, result.lastName)

        verify { userRepository.findByEmail(userDTO.email) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth throws when user not found and consent null`() {
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByEmail(userDTO.email) } returns null

        val ex = assertThrows<RHException> {
            userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = null)
        }
        assertEquals(ErrorType.USER_CONSENT_REQUIRED, ex.errorType)
        verify { userRepository.findByEmail(userDTO.email) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth throws when user not found and consent false`() {
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByEmail(userDTO.email) } returns null

        val ex = assertThrows<RHException> {
            userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = false)
        }
        assertEquals(ErrorType.USER_CONSENT_REQUIRED, ex.errorType)
        verify { userRepository.findByEmail(userDTO.email) }
    }

    @Test
    fun `test findOrCreateUserFromOAuth creates new user when consent true`() {
        val userDTO = TestObjectBuilder.getUserDTO(firstName = "Alice", lastName = "Wonder", urlPhoto = "pic.png")

        every { userRepository.findByEmail(userDTO.email) } returns null

        val captured: CapturingSlot<com.yb.rh.entities.User> = slot()
        every { userRepository.save(capture(captured)) } answers { captured.captured }

        val result = userService.findOrCreateUserFromOAuth(userDTO, agreedConsent = true)

        assertNotNull(result)
        assertEquals(userDTO.email, result.email)
        assertEquals("", result.pushNotificationToken) // initialized empty by service for new users
        assertEquals(userDTO.urlPhoto, result.urlPhoto)
        assertEquals(userDTO.firstName, result.firstName)
        assertEquals(userDTO.lastName, result.lastName)

        // Verify the saved entity fields
        assertTrue(captured.isCaptured)
        assertEquals(userDTO.email, captured.captured.email)
        assertEquals("", captured.captured.pushNotificationToken)
        assertEquals(userDTO.urlPhoto, captured.captured.urlPhoto)

        verify { userRepository.findByEmail(userDTO.email) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test getUserDTOByToken success`() {
        val user = TestObjectBuilder.getUser(userId = 1L)

        every { currentUserService.getCurrentUser() } returns user

        val result = userService.getUserDTOByToken()

        assertNotNull(result)
        assertEquals(user.userId, result.id)
        assertEquals(user.email, result.email)
        assertEquals(user.firstName, result.firstName)
        assertEquals(user.lastName, result.lastName)
        verify { currentUserService.getCurrentUser() }
    }

    @Test
    fun `test getUserById success`() {
        val userId = 1L
        val user = TestObjectBuilder.getUser(userId = userId)

        every { userRepository.findByUserId(userId) } returns user

        val result = userService.getUserById(userId)

        assertNotNull(result)
        assertEquals(user.userId, result.userId)
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test getUserById not found`() {
        val userId = 1L

        every { userRepository.findByUserId(userId) } returns null

        assertThrows<RHException> {
            userService.getUserById(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test updateUser success`() {
        val userDTO = TestObjectBuilder.getUserDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        val updatedUser = mockk<com.yb.rh.entities.User>(relaxed = true)
        val updatedUserDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByUserId(userDTO.id) } returns user
        every { user.copy(
            firstName = any(),
            lastName = any(),
            email = any(),
            pushNotificationToken = any(),
            urlPhoto = any(),
            isActive = any(),
            creationTime = any(),
            updateTime = any(),
            userId = any()
        ) } returns updatedUser
        every { userRepository.save(updatedUser) } returns updatedUser
        every { updatedUser.toDto() } returns updatedUserDTO

        val result = userService.updateUser(userDTO)

        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        verify { userRepository.findByUserId(userDTO.id) }
        verify { userRepository.save(updatedUser) }
    }

    @Test
    fun `test updateUser not found`() {
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByUserId(userDTO.id) } returns null

        assertThrows<RHException> {
            userService.updateUser(userDTO)
        }
        verify { userRepository.findByUserId(userDTO.id) }
    }

    @Test
    fun `test deActivateUser success`() {
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)

        every { currentUserService.getCurrentUser() } returns user
        every { userRepository.save(user) } returns user

        userService.deActivateUser()

        verify { currentUserService.getCurrentUser() }
        verify { user.isActive = false }
        verify { userRepository.save(user) }
    }

    @Test
    fun `test activateUser success`() {
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)

        every { currentUserService.getCurrentUser() } returns user
        every { userRepository.save(user) } returns user

        userService.activateUser()

        verify { currentUserService.getCurrentUser() }
        verify { user.isActive = true }
        verify { userRepository.save(user) }
    }

    @Test
    fun `test updatePushNotificationToken success`() {
        val newToken = "ExponentPushToken[new-token-456]"
        val currentUser = mockk<com.yb.rh.entities.User>(relaxed = true)
        val updatedUser = mockk<com.yb.rh.entities.User>(relaxed = true)
        val expectedUserDTO = TestObjectBuilder.getUserDTO(id = 1L, pushNotificationToken = newToken)

        every { currentUserService.getCurrentUser() } returns currentUser
        every { currentUser.copy(pushNotificationToken = newToken) } returns updatedUser
        every { userRepository.save(updatedUser) } returns updatedUser
        every { updatedUser.toDto() } returns expectedUserDTO

        val result = userService.updatePushNotificationToken(newToken)

        assertNotNull(result)
        assertEquals(newToken, result.pushNotificationToken)
        assertEquals(expectedUserDTO.id, result.id)
        verify { currentUserService.getCurrentUser() }
        verify { userRepository.save(updatedUser) }
    }
} 