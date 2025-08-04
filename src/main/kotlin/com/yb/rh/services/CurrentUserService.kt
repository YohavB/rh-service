package com.yb.rh.services

import com.yb.rh.entities.User
import com.yb.rh.error.RHException
import com.yb.rh.repositories.UserRepository
import com.yb.rh.security.JwtTokenProvider
import com.yb.rh.utils.countryCarJson.logger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Service
class CurrentUserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    /**
     * Gets the current authenticated user from the SecurityContext
     * @return The current authenticated user
     * @throws RHException if no user is authenticated or user not found
     */
    fun getCurrentUser(): User {
        val userId = getCurrentUserId()
        return userRepository.findByUserId(userId)
            ?: throw RHException("Authenticated user not found in database")
    }

    /**
     * Gets the current authenticated user ID from the JWT token
     * @return The current authenticated user ID
     * @throws RHException if no user is authenticated
     */
    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated) {
            throw RHException("No authenticated user found")
        }

        // Extract user ID from the current request's Authorization header
        val request = getCurrentRequest()
        val authHeader = request?.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            val userId = jwtTokenProvider.getUserIdFromToken(token)

            if (userId != null) {
                return userId
            }
        }

        // Fallback: extract from authentication name (email) and find user
        val username = authentication.name
        val user = userRepository.findByEmail(username)
            ?: throw RHException("Authenticated user not found in database")

        return user.userId
    }

    /**
     * Safely gets the current authenticated user, returns null if not authenticated
     * @return The current authenticated user or null if not authenticated
     */
    fun getCurrentUserOrNull(): User? {
        return try {
            getCurrentUser()
        } catch (e: Exception) {
            logger.error(e) { "Error getting current user" }
            null
        }
    }

    /**
     * Gets the current HTTP request
     * @return The current HttpServletRequest or null if not available
     */
    private fun getCurrentRequest(): HttpServletRequest? {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            requestAttributes?.request
        } catch (e: Exception) {
            logger.error(e) { "Exception occurred while requesting user" }
            null
        }
    }
} 