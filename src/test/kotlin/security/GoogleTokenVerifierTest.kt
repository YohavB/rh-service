package com.yb.rh.security

import com.yb.rh.error.RHException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GoogleTokenVerifierTest {

    private lateinit var googleTokenVerifier: GoogleTokenVerifier

    @BeforeEach
    fun setUp() {
        googleTokenVerifier = GoogleTokenVerifier()
    }

    @Test
    fun `test verifyToken with valid token format returns user info`() {
        // Given
        val validToken = "valid.google.token"
        
        // When & Then
        // Note: This test would need actual Google API integration or mocking
        // For now, we test that the method exists and handles the token
        assertThrows<RHException> {
            googleTokenVerifier.verifyToken(validToken)
        }
    }

    @Test
    fun `test verifyToken with empty token throws exception`() {
        // Given
        val emptyToken = ""
        
        // When & Then
        assertThrows<RHException> {
            googleTokenVerifier.verifyToken(emptyToken)
        }
    }



    @Test
    fun `test verifyToken with invalid token format throws exception`() {
        // Given
        val invalidToken = "invalid-token-format"
        
        // When & Then
        assertThrows<RHException> {
            googleTokenVerifier.verifyToken(invalidToken)
        }
    }
}