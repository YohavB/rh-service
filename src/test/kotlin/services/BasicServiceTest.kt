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
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        userService = UserService(userRepository)
    }

    @Test
    fun `test createUser success`() {
        // Given
        val userCreationDTO = TestObjectBuilder.getUserCreationDTO()
        val user = mockk<com.yb.rh.entities.User>()
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { userRepository.save(any()) } returns user
        every { user.toDto() } returns userDTO

        // When
        val result = userService.createUser(userCreationDTO)

        // Then
        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        assertEquals(userDTO.email, result.email)
        verify { userRepository.save(any()) }
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