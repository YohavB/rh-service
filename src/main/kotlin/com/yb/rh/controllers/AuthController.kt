package com.yb.rh.controllers

import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.enum.OAuthProvider
import com.yb.rh.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller handling OAuth2 authentication operations
 * Supports Google, Facebook, and Apple sign-in with JWT token management
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    /**
     * Google OAuth2 login endpoint
     * @param request OAuth login request containing the Google ID token
     * @param agreedConsent Optional consent parameter for new users (required for first-time login)
     * @return Authentication response with JWT token and user information
     * @throws RHException with USER_CONSENT_REQUIRED error for new users without consent
     */
    @PostMapping("/google")
    fun googleLogin(
        @Valid @RequestBody request: OAuthLoginRequestDTO,
        @RequestParam(required = false) agreedConsent: Boolean
    ): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.GOOGLE, agreedConsent)
        return ResponseEntity.ok(result)
    }
    
    /**
     * Facebook OAuth2 login endpoint
     * @param request OAuth login request containing the Facebook access token
     * @param agreedConsent Optional consent parameter for new users (required for first-time login)
     * @return Authentication response with JWT token and user information
     * @throws RHException with USER_CONSENT_REQUIRED error for new users without consent
     */
    @PostMapping("/facebook")
    fun facebookLogin(
        @Valid @RequestBody request: OAuthLoginRequestDTO,
        @RequestParam(required = false) agreedConsent: Boolean
    ): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.FACEBOOK, agreedConsent)
        return ResponseEntity.ok(result)
    }
    
    /**
     * Apple Sign In endpoint
     * @param request OAuth login request containing the Apple ID token
     * @param agreedConsent Optional consent parameter for new users (required for first-time login)
     * @return Authentication response with JWT token and user information
     * @throws RHException with USER_CONSENT_REQUIRED error for new users without consent
     */
    @PostMapping("/apple")
    fun appleLogin(
        @Valid @RequestBody request: OAuthLoginRequestDTO,
        @RequestParam(required = false) agreedConsent: Boolean
    ): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.APPLE, agreedConsent)
        return ResponseEntity.ok(result)
    }
    
    /**
     * Refresh JWT token endpoint
     * @return New authentication response with refreshed JWT token and user information
     * @throws RHException if current token is invalid or expired
     */
    @PostMapping("/refresh")
    fun refreshToken(): ResponseEntity<AuthResponseDTO> {
        val result = authService.refreshToken()
        return ResponseEntity.ok(result)
    }
    
    /**
     * Logout endpoint (client-side token invalidation)
     * In a stateless JWT setup, logout is handled client-side by removing the token
     * @return 200 OK response (client should discard the JWT token)
     */
    @PostMapping("/logout")
    fun logout() {
        val result = authService.logout()
    }
} 