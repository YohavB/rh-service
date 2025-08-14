package com.yb.rh.security

import com.yb.rh.TestObjectBuilder
import com.yb.rh.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CustomUserDetailsServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var customUserDetailsService: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        customUserDetailsService = CustomUserDetailsService(userRepository)
    }

    @Test
    fun `test loadUserByUsername with existing user returns UserDetails`() {
        // Given
        val email = "test@example.com"
        val user = TestObjectBuilder.getUser(email = email)
        
        every { userRepository.findByEmail(email) } returns user

        // When
        val userDetails = customUserDetailsService.loadUserByUsername(email)

        // Then
        assertNotNull(userDetails)
        assertEquals(email, userDetails.username)
        assertEquals("", userDetails.password) // Password is empty for OAuth users
        assertEquals(2, userDetails.authorities.size) // ROLE_USER and ROLE_ACTIVE_USER
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test loadUserByUsername with non-existing user throws UsernameNotFoundException`() {
        // Given
        val email = "nonexistent@example.com"
        
        every { userRepository.findByEmail(email) } returns null

        // When & Then
        assertThrows<UsernameNotFoundException> {
            customUserDetailsService.loadUserByUsername(email)
        }
        
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test loadUserById with existing user returns UserDetails`() {
        // Given
        val userId = 1L
        val user = TestObjectBuilder.getUser(userId = userId)
        
        every { userRepository.findByUserId(userId) } returns user

        // When
        val userDetails = customUserDetailsService.loadUserById(userId)

        // Then
        assertNotNull(userDetails)
        assertEquals(user.email, userDetails.username)
        assertEquals("", userDetails.password)
        verify { userRepository.findByUserId(userId) }
    }
}