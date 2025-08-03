package com.yb.rh.services

import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.enum.OAuthProvider
import com.yb.rh.security.AppleTokenVerifier
import com.yb.rh.security.FacebookTokenVerifier
import com.yb.rh.security.GoogleTokenVerifier
import com.yb.rh.security.JwtTokenProvider
import com.yb.rh.utils.maskEmail
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val googleTokenVerifier: GoogleTokenVerifier,
    private val facebookTokenVerifier: FacebookTokenVerifier,
    private val appleTokenVerifier: AppleTokenVerifier,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService
) {
    private val logger = KotlinLogging.logger {}

    fun login(request: OAuthLoginRequestDTO, oAuthProvider: OAuthProvider): AuthResponseDTO {
        logger.info { "Login attempt for provider: $oAuthProvider with token: ${request.token.take(20)}..." }
        logger.debug { "Processing OAuth login for provider: $oAuthProvider" }

        val userDTO: UserDTO = when (oAuthProvider) {
            OAuthProvider.GOOGLE -> {
                logger.debug { "Processing Google OAuth login" }
                googleLogin(request)
            }
            OAuthProvider.FACEBOOK -> {
                logger.debug { "Processing Facebook OAuth login" }
                facebookLogin(request)
            }
            OAuthProvider.APPLE -> {
                logger.debug { "Processing Apple OAuth login" }
                appleLogin(request)
            }
        }

        logger.debug { "OAuth verification completed, userDTO: ${userDTO.email.maskEmail()}" }
        val user = userService.findOrCreateUserFromOAuth(userDTO)

        logger.info { "About to generate JWT token..." }
        logger.debug { "Generating JWT token for user: ${user.email.maskEmail()}" }
        val jwtToken = jwtTokenProvider.generateToken(user.userId, user.email)

        logger.info { "Successful login for user: ${user.email.maskEmail()}" }
        logger.debug { "Login process completed successfully for user: ${user.email.maskEmail()}" }

        return AuthResponseDTO(
            token = jwtToken,
            user = user.toDto()
        )
    }

    private fun googleLogin(request: OAuthLoginRequestDTO): UserDTO {
        logger.debug { "Verifying Google token..." }
        val googleUserInfo = googleTokenVerifier.verifyToken(request.token)
        logger.debug { "Google token verified successfully for email: ${googleUserInfo.email?.maskEmail()}" }
        return googleUserInfo.toUserDTO()
    }

    private fun facebookLogin(request: OAuthLoginRequestDTO): UserDTO {
        logger.debug { "Verifying Facebook token..." }
        val facebookUserInfo = facebookTokenVerifier.verifyToken(request.token)
        logger.debug { "Facebook token verified successfully for email: ${facebookUserInfo.email?.maskEmail()}" }
        return facebookUserInfo.toUserDTO()
    }

    private fun appleLogin(request: OAuthLoginRequestDTO): UserDTO {
        logger.debug { "Verifying Apple token..." }
        val appleUserInfo = appleTokenVerifier.verifyToken(request.token)
        logger.debug { "Apple token verified successfully for email: ${appleUserInfo.email?.maskEmail()}" }
        return appleUserInfo.toUserDTO()
    }

    fun refreshToken(authHeader: String): AuthResponseDTO {
        val token = authHeader.removePrefix("Bearer ")

        return try {
            val userId = jwtTokenProvider.getUserIdFromToken(token)
                ?: throw IllegalArgumentException("Invalid token")

            val user = userService.getUserById(userId)
            val newToken = jwtTokenProvider.generateToken(user.userId, user.email)

            AuthResponseDTO(
                token = newToken,
                user = user.toDto()
            )
        } catch (ex: Exception) {
            logger.warn(ex) { "Token refresh failed" }
            throw ex
        }
    }

    fun logout(): Map<String, String> {
        // In a stateless JWT setup, logout is handled client-side by removing the token
        // You might want to implement a token blocklist for additional security
        logger.info { "User logout" }
        return mapOf("message" to "Logged out successfully")
    }
}