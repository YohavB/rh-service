package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.enum.OAuthProvider
import com.yb.rh.services.AuthService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
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
        
        every { authService.login(request, OAuthProvider.GOOGLE) } returns expectedResponse

        // When
        val result = authController.googleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.login(request, OAuthProvider.GOOGLE) }
    }

    @Test
    fun `test googleLogin failure - service throws exception`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-token")
        
        every { authService.login(request, OAuthProvider.GOOGLE) } throws RuntimeException("Invalid token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.googleLogin(request)
        }
        verify { authService.login(request, OAuthProvider.GOOGLE) }
    }

    @Test
    fun `test facebookLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("facebook-access-token")
        val expectedResponse = AuthResponseDTO(
            token = "jwt-token",
            user = TestObjectBuilder.getUserDTO(email = "test@example.com")
        )
        
        every { authService.login(request, OAuthProvider.FACEBOOK) } returns expectedResponse

        // When
        val result = authController.facebookLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.login(request, OAuthProvider.FACEBOOK) }
    }

    @Test
    fun `test appleLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("apple-id-token")
        val expectedResponse = AuthResponseDTO(
            token = "jwt-token",
            user = TestObjectBuilder.getUserDTO(email = "test@example.com")
        )
        
        every { authService.login(request, OAuthProvider.APPLE) } returns expectedResponse

        // When
        val result = authController.appleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.login(request, OAuthProvider.APPLE) }
    }

    @Test
    fun `test refreshToken success`() {
        // Given
        val expectedResponse = AuthResponseDTO(
            token = "new-jwt-token",
            user = TestObjectBuilder.getUserDTO(id = 1L)
        )
        
        every { authService.refreshToken() } returns expectedResponse

        // When
        val result = authController.refreshToken()

        // Then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertEquals(expectedResponse.token, result.body!!.token)
        assertEquals(expectedResponse.user, result.body!!.user)
        verify { authService.refreshToken() }
    }

    @Test
    fun `test refreshToken failure - service throws exception`() {
        // Given
        
        every { authService.refreshToken() } throws IllegalArgumentException("Invalid token")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authController.refreshToken()
        }
        verify { authService.refreshToken() }
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
        
        every { authService.login(request, OAuthProvider.GOOGLE) } throws RuntimeException("Empty token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.googleLogin(request)
        }
        verify { authService.login(request, OAuthProvider.GOOGLE) }
    }

    @Test
    fun `test facebookLogin with invalid token`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-facebook-token")
        
        every { authService.login(request, OAuthProvider.FACEBOOK) } throws RuntimeException("Invalid Facebook token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.facebookLogin(request)
        }
        verify { authService.login(request, OAuthProvider.FACEBOOK) }
    }

    @Test
    fun `test appleLogin with invalid token`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-apple-token")
        
        every { authService.login(request, OAuthProvider.APPLE) } throws RuntimeException("Invalid Apple token")

        // When & Then
        assertThrows<RuntimeException> {
            authController.appleLogin(request)
        }
        verify { authService.login(request, OAuthProvider.APPLE) }
    }

    @Test
    fun `test refreshToken with authentication failure`() {
        // Given
        
        every { authService.refreshToken() } throws IllegalArgumentException("No authenticated user")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authController.refreshToken()
        }
        verify { authService.refreshToken() }
    }

    @Test
    fun `test refreshToken with service error`() {
        // Given
        
        every { authService.refreshToken() } throws RuntimeException("Service error")

        // When & Then
        assertThrows<RuntimeException> {
            authController.refreshToken()
        }
        verify { authService.refreshToken() }
    }
} 