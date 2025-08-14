package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.*
import com.yb.rh.error.RHException
import com.yb.rh.security.AppleTokenVerifier
import com.yb.rh.security.FacebookTokenVerifier
import com.yb.rh.security.GoogleTokenVerifier
import com.yb.rh.security.JwtTokenProvider
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
    private lateinit var currentUserService: CurrentUserService
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        googleTokenVerifier = mockk()
        facebookTokenVerifier = mockk()
        appleTokenVerifier = mockk()
        jwtTokenProvider = mockk()
        userService = mockk()
        currentUserService = mockk()
        authService = AuthService(
            googleTokenVerifier,
            facebookTokenVerifier,
            appleTokenVerifier,
            jwtTokenProvider,
            userService,
            currentUserService
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
            picture = "https://example.com/photo.jpg"
        )
        
        every { googleTokenVerifier.verifyToken(request.token) } returns googleUserInfo

        // When
        val result = authService.googleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals("test@example.com", result.email)
        assertEquals("Test", result.firstName)
        assertEquals("User", result.lastName)
        assertEquals("https://example.com/photo.jpg", result.urlPhoto)
        verify { 
            googleTokenVerifier.verifyToken(request.token)
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
        
        every { facebookTokenVerifier.verifyToken(request.token) } returns facebookUserInfo

        // When
        val result = authService.facebookLogin(request)

        // Then
        assertNotNull(result)
        assertEquals("test@example.com", result.email)
        assertEquals("Test", result.firstName)
        assertEquals("User", result.lastName)
        assertEquals("https://example.com/photo.jpg", result.urlPhoto)
        verify { 
            facebookTokenVerifier.verifyToken(request.token)
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
            name = "Test User"
        )
        
        every { appleTokenVerifier.verifyToken(request.token) } returns appleUserInfo

        // When
        val result = authService.appleLogin(request)

        // Then
        assertNotNull(result)
        assertEquals("test@example.com", result.email)
        assertEquals("Test", result.firstName)
        assertEquals("User", result.lastName)
        assertEquals(null, result.urlPhoto)
        verify { 
            appleTokenVerifier.verifyToken(request.token)
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
        val user = TestObjectBuilder.getUser(userId = 1L)
        val newJwtToken = "new-jwt-token"
        
        every { currentUserService.getCurrentUser() } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } returns newJwtToken

        // When
        val result = authService.refreshToken()

        // Then
        assertNotNull(result)
        assertEquals(newJwtToken, result.token)
        assertEquals(user.toDto(), result.user)
        verify { 
            currentUserService.getCurrentUser()
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }

    @Test
    fun `test refreshToken failure - no authenticated user`() {
        // Given
        
        every { currentUserService.getCurrentUser() } throws IllegalArgumentException("No authenticated user")

        // When & Then
        assertThrows<IllegalArgumentException> {
            authService.refreshToken()
        }
        verify { currentUserService.getCurrentUser() }
    }

    @Test
    fun `test logout success`() {
        // Given
        val user = TestObjectBuilder.getUser()
        every { currentUserService.getCurrentUser() } returns user
        
        // When
        authService.logout()

        // Then
        // Logout completed successfully (no return value expected)
        verify { currentUserService.getCurrentUser() }
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
    fun `test refreshToken with service error`() {
        // Given
        
        every { currentUserService.getCurrentUser() } throws RuntimeException("Service error")

        // When & Then
        assertThrows<RuntimeException> {
            authService.refreshToken()
        }
        verify { currentUserService.getCurrentUser() }
    }

    @Test
    fun `test refreshToken with token generation error`() {
        // Given
        val user = TestObjectBuilder.getUser(userId = 1L)
        
        every { currentUserService.getCurrentUser() } returns user
        every { jwtTokenProvider.generateToken(user.userId, user.email) } throws RuntimeException("Token generation failed")

        // When & Then
        assertThrows<RuntimeException> {
            authService.refreshToken()
        }
        verify { 
            currentUserService.getCurrentUser()
            jwtTokenProvider.generateToken(user.userId, user.email)
        }
    }
} 