package com.yb.rh.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {
    
    private lateinit var jwtTokenProvider: JwtTokenProvider
    
    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider()
        
        // Manually set the properties using reflection
        val secretField = JwtTokenProvider::class.java.getDeclaredField("jwtSecret")
        secretField.isAccessible = true
        secretField.set(jwtTokenProvider, "test-secret-key-for-testing-purposes-only-this-is-64-bytes-long-for-security")
        
        val expirationField = JwtTokenProvider::class.java.getDeclaredField("jwtExpiration")
        expirationField.isAccessible = true
        expirationField.set(jwtTokenProvider, 3600000L)
    }
    
    @Test
    fun `should generate and validate token`() {
        // Given
        val userId = 1L
        val email = "test@example.com"
        
        // When
        val token = jwtTokenProvider.generateToken(userId, email)
        
        // Then
        assertNotNull(token)
        assertTrue(jwtTokenProvider.validateToken(token))
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token))
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token))
    }
    
    @Test
    fun `should reject invalid token`() {
        // Given
        val invalidToken = "invalid.token.here"
        
        // When & Then
        assertFalse(jwtTokenProvider.validateToken(invalidToken))
        assertNull(jwtTokenProvider.getUserIdFromToken(invalidToken))
        assertNull(jwtTokenProvider.getEmailFromToken(invalidToken))
    }
    
    @Test
    fun `should reject empty token`() {
        // Given
        val emptyToken = ""
        
        // When & Then
        assertFalse(jwtTokenProvider.validateToken(emptyToken))
    }
} 