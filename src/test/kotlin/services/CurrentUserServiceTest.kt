package com.yb.rh.services

import com.yb.rh.entities.User
import com.yb.rh.error.RHException
import com.yb.rh.repositories.UserRepository
import com.yb.rh.security.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User as SpringUser

class CurrentUserServiceTest {
    
    private lateinit var currentUserService: CurrentUserService
    private lateinit var userRepository: UserRepository
    
    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        val jwtTokenProvider = mockk<JwtTokenProvider>()
        currentUserService = CurrentUserService(userRepository, jwtTokenProvider)
        SecurityContextHolder.clearContext()
        
        // Mock the JWT token provider to return null (no token in test context)
        every { jwtTokenProvider.getUserIdFromToken(any()) } returns null
    }
    
    @Test
    fun `getCurrentUser should return user when authenticated`() {
        // Given
        val testUser = User(
            userId = 1L,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "",
            urlPhoto = null,
            isActive = true
        )
        
        val springUser = SpringUser.builder()
            .username("john.doe@example.com")
            .password("")
            .authorities(listOf())
            .build()
        
        val authentication = UsernamePasswordAuthenticationToken(springUser, null, springUser.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        
        // Mock the repository to return the test user
        every { userRepository.findByEmail("john.doe@example.com") } returns testUser
        every { userRepository.findByUserId(1L) } returns testUser
        
        // When
        val result = currentUserService.getCurrentUser()
        
        // Then
        assert(result == testUser)
        verify { userRepository.findByEmail("john.doe@example.com") }
    }
    
    @Test
    fun `getCurrentUser should throw exception when not authenticated`() {
        // When & Then
        assertThrows<RHException> {
            currentUserService.getCurrentUser()
        }
    }
    
    @Test
    fun `getCurrentUser should throw exception when user not found in database`() {
        // Given
        val springUser = SpringUser.builder()
            .username("nonexistent@example.com")
            .password("")
            .authorities(listOf())
            .build()
        
        val authentication = UsernamePasswordAuthenticationToken(springUser, null, springUser.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        
        every { userRepository.findByEmail("nonexistent@example.com") } returns null
        
        // When & Then
        assertThrows<RHException> {
            currentUserService.getCurrentUser()
        }
    }
    
    @Test
    fun `getCurrentUserId should return user ID when authenticated`() {
        // Given
        val testUser = User(
            userId = 1L,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "",
            urlPhoto = null,
            isActive = true
        )
        
        val springUser = SpringUser.builder()
            .username("john.doe@example.com")
            .password("")
            .authorities(listOf())
            .build()
        
        val authentication = UsernamePasswordAuthenticationToken(springUser, null, springUser.authorities)
        SecurityContextHolder.getContext().authentication = authentication
        
        every { userRepository.findByEmail("john.doe@example.com") } returns testUser
        
        // When
        val result = currentUserService.getCurrentUserId()
        
        // Then
        assert(result == 1L)
    }
    
    @Test
    fun `getCurrentUserOrNull should return null when not authenticated`() {
        // When
        val result = currentUserService.getCurrentUserOrNull()
        
        // Then
        assert(result == null)
    }
} 