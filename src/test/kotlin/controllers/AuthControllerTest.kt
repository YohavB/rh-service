package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.services.AuthService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthControllerTest {
    private lateinit var authService: AuthService
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        authService = mockk()
        authController = AuthController(authService)
    }

    @Test
    fun `test googleLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("google-id-token")
        val expectedResponse = AuthResponseDTO(
            token = "jwt-token",
            user = TestObjectBuilder.getUserDTO(email = "test@example.com")
        )
        
        every { authService.googleLogin(request) } returns expectedResponse

        // When
        val result = authController.googleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.googleLogin(request) }
    }

    @Test
    fun `test googleLogin failure - service throws exception`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-token")
        
        every { authService.googleLogin(request) } throws RuntimeException("Invalid token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.googleLogin(request)
        }
        verify { authService.googleLogin(request) }
    }

    @Test
    fun `test facebookLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("facebook-access-token")
        val expectedResponse = AuthResponseDTO(
            token = "jwt-token",
            user = TestObjectBuilder.getUserDTO(email = "test@example.com")
        )
        
        every { authService.facebookLogin(request) } returns expectedResponse

        // When
        val result = authController.facebookLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.facebookLogin(request) }
    }

    @Test
    fun `test appleLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("apple-id-token")
        val expectedResponse = AuthResponseDTO(
            token = "jwt-token",
            user = TestObjectBuilder.getUserDTO(email = "test@example.com")
        )
        
        every { authService.appleLogin(request) } returns expectedResponse

        // When
        val result = authController.appleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.appleLogin(request) }
    }

    @Test
    fun `test refreshToken success`() {
        // Given
        val authHeader = "Bearer valid-jwt-token"
        val expectedResponse = AuthResponseDTO(
            token = "new-jwt-token",
            user = TestObjectBuilder.getUserDTO(id = 1L)
        )
        
        every { authService.refreshToken(authHeader) } returns expectedResponse

        // When
        val result = authController.refreshToken(authHeader)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.refreshToken(authHeader) }
    }

    @Test
    fun `test refreshToken failure - service throws exception`() {
        // Given
        val authHeader = "Bearer invalid-jwt-token"
        
        every { authService.refreshToken(authHeader) } throws IllegalArgumentException("Invalid token")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authController.refreshToken(authHeader)
        }
        verify { authService.refreshToken(authHeader) }
    }

    @Test
    fun `test logout success`() {
        // Given
        val expectedResponse = mapOf("message" to "Logged out successfully")
        
        every { authService.logout() } returns expectedResponse

        // When
        val result = authController.logout()

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse, result.body)
        verify { authService.logout() }
    }

    @Test
    fun `test googleLogin with empty token`() {
        // Given
        val request = OAuthLoginRequestDTO("")
        
        every { authService.googleLogin(request) } throws RuntimeException("Empty token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.googleLogin(request)
        }
        verify { authService.googleLogin(request) }
    }

    @Test
    fun `test facebookLogin with invalid token`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-facebook-token")
        
        every { authService.facebookLogin(request) } throws RuntimeException("Invalid Facebook token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.facebookLogin(request)
        }
        verify { authService.facebookLogin(request) }
    }

    @Test
    fun `test appleLogin with invalid token`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-apple-token")
        
        every { authService.appleLogin(request) } throws RuntimeException("Invalid Apple token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.appleLogin(request)
        }
        verify { authService.appleLogin(request) }
    }

    @Test
    fun `test refreshToken with malformed header`() {
        // Given
        val authHeader = "InvalidHeader"
        
        every { authService.refreshToken(authHeader) } throws IllegalArgumentException("Invalid header")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authController.refreshToken(authHeader)
        }
        verify { authService.refreshToken(authHeader) }
    }

    @Test
    fun `test refreshToken with empty token`() {
        // Given
        val authHeader = "Bearer "
        
        every { authService.refreshToken(authHeader) } throws IllegalArgumentException("Empty token")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authController.refreshToken(authHeader)
        }
        verify { authService.refreshToken(authHeader) }
    }
} 