package com.yb.rh.controllers

import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.security.GoogleTokenVerifier
import com.yb.rh.security.FacebookTokenVerifier
import com.yb.rh.security.AppleTokenVerifier
import com.yb.rh.security.JwtTokenProvider
import com.yb.rh.security.OAuthProvider
import com.yb.rh.services.UserService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val facebookTokenVerifier: FacebookTokenVerifier,
    private val appleTokenVerifier: AppleTokenVerifier,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) : BaseController() {
    
    @PostMapping("/google")
    fun googleLogin(@Valid @RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        logger.info { "Google login attempt for email: ${request.token.take(20)}..." }
        
        return try {
            // Verify Google ID token
            val googleUserInfo = googleTokenVerifier.verifyToken(request.token)
            
            // Find or create user
            val user = userService.findOrCreateUserFromOAuth(googleUserInfo, OAuthProvider.GOOGLE)
            
            // Generate JWT token
            val jwtToken = jwtTokenProvider.generateToken(user.userId, user.email)
            
            logger.info { "Successful Google login for user: ${user.email}" }
            
            ResponseEntity.ok(AuthResponseDTO(
                token = jwtToken,
                user = user.toDto()
            ))
        } catch (ex: Exception) {
            logger.warn(ex) { "Google login failed" }
            throw ex
        }
    }
    
    @PostMapping("/facebook")
    fun facebookLogin(@Valid @RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        logger.info { "Facebook login attempt for token: ${request.token.take(20)}..." }
        
        return try {
            // Verify Facebook access token
            val facebookUserInfo = facebookTokenVerifier.verifyToken(request.token)
            val googleUserInfo = facebookUserInfo.toGoogleUserInfo()
            
            // Find or create user
            val user = userService.findOrCreateUserFromOAuth(googleUserInfo, OAuthProvider.FACEBOOK)
            
            // Generate JWT token
            val jwtToken = jwtTokenProvider.generateToken(user.userId, user.email)
            
            logger.info { "Successful Facebook login for user: ${user.email}" }
            
            ResponseEntity.ok(AuthResponseDTO(
                token = jwtToken,
                user = user.toDto()
            ))
        } catch (ex: Exception) {
            logger.warn(ex) { "Facebook login failed" }
            throw ex
        }
    }
    
    @PostMapping("/apple")
    fun appleLogin(@Valid @RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        logger.info { "Apple login attempt for token: ${request.token.take(20)}..." }
        
        return try {
            // Verify Apple ID token
            val appleUserInfo = appleTokenVerifier.verifyToken(request.token)
            val googleUserInfo = appleUserInfo.toGoogleUserInfo()
            
            // Find or create user
            val user = userService.findOrCreateUserFromOAuth(googleUserInfo, OAuthProvider.APPLE)
            
            // Generate JWT token
            val jwtToken = jwtTokenProvider.generateToken(user.userId, user.email)
            
            logger.info { "Successful Apple login for user: ${user.email}" }
            
            ResponseEntity.ok(AuthResponseDTO(
                token = jwtToken,
                user = user.toDto()
            ))
        } catch (ex: Exception) {
            logger.warn(ex) { "Apple login failed" }
            throw ex
        }
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<AuthResponseDTO> {
        val token = authHeader.removePrefix("Bearer ")
        
        return try {
            val userId = jwtTokenProvider.getUserIdFromToken(token)
                ?: throw IllegalArgumentException("Invalid token")
            
            val user = userService.getUserById(userId)
            val newToken = jwtTokenProvider.generateToken(user.userId, user.email)
            
            ResponseEntity.ok(AuthResponseDTO(
                token = newToken,
                user = user.toDto()
            ))
        } catch (ex: Exception) {
            logger.warn(ex) { "Token refresh failed" }
            throw ex
        }
    }
    
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        // In a stateless JWT setup, logout is handled client-side by removing the token
        // You might want to implement a token blacklist for additional security
        logger.info { "User logout" }
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
} 