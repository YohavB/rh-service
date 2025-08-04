package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BasicServiceTest {

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
    fun `test getUserDTOByToken success`() {
        // Given
        val user = TestObjectBuilder.getUser(userId = 1L)
        
        every { currentUserService.getCurrentUser() } returns user

        // When
        val result = userService.getUserDTOByToken()

        // Then
        assertNotNull(result)
        assertEquals(user.userId, result.id)
        assertEquals(user.email, result.email)
        assertEquals(user.firstName, result.firstName)
        assertEquals(user.lastName, result.lastName)
        verify { currentUserService.getCurrentUser() }
    }

    @Test
    fun `test getUserById success`() {
        // Given
        val userId = 1L
        val user = TestObjectBuilder.getUser(userId = userId)
        
        every { userRepository.findByUserId(userId) } returns user

        // When
        val result = userService.getUserById(userId)

        // Then
        assertNotNull(result)
        assertEquals(user.userId, result.userId)
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test getUserById not found`() {
        // Given
        val userId = 999L
        
        every { userRepository.findByUserId(userId) } returns null

        // When & Then
        assertThrows<com.yb.rh.error.RHException> {
            userService.getUserById(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }
} 