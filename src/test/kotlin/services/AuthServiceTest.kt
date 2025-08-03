package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.AppleUserInfoDTO
import com.yb.rh.dtos.FacebookPicture
import com.yb.rh.dtos.FacebookPictureData
import com.yb.rh.dtos.FacebookUserInfoDTO
import com.yb.rh.dtos.GoogleUserInfoDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.error.RHException
import com.yb.rh.security.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthServiceTest {
    private lateinit var googleTokenVerifier: GoogleTokenVerifier
    private lateinit var facebookTokenVerifier: FacebookTokenVerifier
    private lateinit var appleTokenVerifier: AppleTokenVerifier
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var userService: UserService
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        googleTokenVerifier = mockk()
        facebookTokenVerifier = mockk()
        appleTokenVerifier = mockk()
        jwtTokenProvider = mockk()
        userService = mockk()
        authService = AuthService(
            googleTokenVerifier,
            facebookTokenVerifier,
            appleTokenVerifier,
            jwtTokenProvider,
            userService
        )
    }

    @Test
    fun `test googleLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("google-id-token")
        val googleUserInfo = GoogleUserInfoDTO(
            email = "test@example.com",
            name = "Test User",
            givenName = "Test",
            familyName = "User",
            picture = "https://example.com/photo.jpg",
            emailVerified = true
        )
        val user = TestObjectBuilder.getUser(email = "test@example.com")
        val jwtToken = "jwt-token"
        
        every { googleTokenVerifier.verifyToken(request.token) } returns googleUserInfo
        every { userService.findOrCreateUserFromGoogle(googleUserInfo) } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } returns jwtToken

        // When
        val result = authService.googleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(jwtToken, result.token)
        assertEquals(user.toDto(), result.user)
        verify { 
            googleTokenVerifier.verifyToken(request.token)
            userService.findOrCreateUserFromGoogle(googleUserInfo)
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }

    @Test
    fun `test googleLogin failure - token verification fails`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-token")
        
        every { googleTokenVerifier.verifyToken(request.token) } throws RHException("Invalid token")

        // When & Then
        assertThrows<RHException> {
            authService.googleLogin(request)
        }
        verify { googleTokenVerifier.verifyToken(request.token) }
    }

    @Test
    fun `test facebookLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("facebook-access-token")
        val facebookUserInfo = FacebookUserInfoDTO(
            id = "123",
            name = "Test User",
            email = "test@example.com",
            fbFirstName = "Test",
            fbLastName = "User",
            fbPicture = FacebookPicture(FacebookPictureData("https://example.com/photo.jpg"))
        )
        val user = TestObjectBuilder.getUser(email = "test@example.com")
        val jwtToken = "jwt-token"
        
        every { facebookTokenVerifier.verifyToken(request.token) } returns facebookUserInfo
        every { userService.findOrCreateUserFromFacebook(facebookUserInfo) } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } returns jwtToken

        // When
        val result = authService.facebookLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(jwtToken, result.token)
        assertEquals(user.toDto(), result.user)
        verify { 
            facebookTokenVerifier.verifyToken(request.token)
            userService.findOrCreateUserFromFacebook(facebookUserInfo)
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }

    @Test
    fun `test facebookLogin failure - token verification fails`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-facebook-token")
        
        every { facebookTokenVerifier.verifyToken(request.token) } throws RHException("Invalid Facebook token")

        // When & Then
        assertThrows<RHException> {
            authService.facebookLogin(request)
        }
        verify { facebookTokenVerifier.verifyToken(request.token) }
    }

    @Test
    fun `test appleLogin success`() {
        // Given
        val request = OAuthLoginRequestDTO("apple-id-token")
        val appleUserInfo = AppleUserInfoDTO(
            sub = "apple_user_id",
            email = "test@example.com",
            name = "Test User",
            emailVerified = true
        )
        val user = TestObjectBuilder.getUser(email = "test@example.com")
        val jwtToken = "jwt-token"
        
        every { appleTokenVerifier.verifyToken(request.token) } returns appleUserInfo
        every { userService.findOrCreateUserFromApple(appleUserInfo) } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } returns jwtToken

        // When
        val result = authService.appleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals(jwtToken, result.token)
        assertEquals(user.toDto(), result.user)
        verify { 
            appleTokenVerifier.verifyToken(request.token)
            userService.findOrCreateUserFromApple(appleUserInfo)
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }

    @Test
    fun `test appleLogin failure - token verification fails`() {
        // Given
        val request = OAuthLoginRequestDTO("invalid-apple-token")
        
        every { appleTokenVerifier.verifyToken(request.token) } throws RHException("Invalid Apple token")

        // When & Then
        assertThrows<RHException> {
            authService.appleLogin(request)
        }
        verify { appleTokenVerifier.verifyToken(request.token) }
    }

    @Test
    fun `test refreshToken success`() {
        // Given
        val authHeader = "Bearer valid-jwt-token"
        val userId = 1L
        val user = TestObjectBuilder.getUser(userId = userId)
        val newJwtToken = "new-jwt-token"
        
        every { jwtTokenProvider.getUserIdFromToken("valid-jwt-token") } returns userId
        every { userService.getUserById(userId) } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } returns newJwtToken

        // When
        val result = authService.refreshToken(authHeader)

        // Then
        assertNotNull(result)
        assertEquals(newJwtToken, result.token)
        assertEquals(user.toDto(), result.user)
        verify { 
            jwtTokenProvider.getUserIdFromToken("valid-jwt-token")
            userService.getUserById(userId)
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }

    @Test
    fun `test refreshToken failure - invalid token`() {
        // Given
        val authHeader = "Bearer invalid-jwt-token"
        
        every { jwtTokenProvider.getUserIdFromToken("invalid-jwt-token") } returns null

        // When & Then
        assertThrows<IllegalArgumentException> {
            authService.refreshToken(authHeader)
        }
        verify { jwtTokenProvider.getUserIdFromToken("invalid-jwt-token") }
    }

    @Test
    fun `test logout success`() {
        // When
        val result = authService.logout()

        // Then
        assertNotNull(result)
        assertEquals("Logged out successfully", result["message"])
    }

    @Test
    fun `test googleLogin with empty token`() {
        // Given
        val request = OAuthLoginRequestDTO("")
        
        every { googleTokenVerifier.verifyToken("") } throws RHException("Empty token")

        // When & Then
        assertThrows<RHException> {
            authService.googleLogin(request)
        }
        verify { googleTokenVerifier.verifyToken("") }
    }

    @Test
    fun `test refreshToken with malformed header`() {
        // Given
        val authHeader = "InvalidHeader"
        
        every { jwtTokenProvider.getUserIdFromToken("InvalidHeader") } returns null

        // When & Then
        assertThrows<IllegalArgumentException> {
            authService.refreshToken(authHeader)
        }
        verify { jwtTokenProvider.getUserIdFromToken("InvalidHeader") }
    }

    @Test
    fun `test refreshToken with empty token`() {
        // Given
        val authHeader = "Bearer "
        
        every { jwtTokenProvider.getUserIdFromToken("") } returns null

        // When & Then
        assertThrows<IllegalArgumentException> {
            authService.refreshToken(authHeader)
        }
        verify { jwtTokenProvider.getUserIdFromToken("") }
    }
} 