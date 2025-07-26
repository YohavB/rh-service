package com.yb.rh.services

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.entities.User
import com.yb.rh.error.RHException
import com.yb.rh.repositories.UserRepository
import com.yb.rh.security.GoogleUserInfo
import com.yb.rh.security.OAuthProvider
import com.yb.rh.utils.maskEmail
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun createUser(userCreationDTO: UserCreationDTO): UserDTO {
        logger.info { "Create user : ${userCreationDTO.email.maskEmail()} " }

        return userRepository.save(userCreationDTO.toEntity()).toDto()
    }

    fun getUserDTOByUserId(userId: Long): UserDTO {
        logger.info { "Try to find user : $userId " }

        return userRepository.findByUserId(userId)
            ?.toDto()
            ?: throw RHException("User not found with id: $userId")

    }

    fun findUserDTOByEmail(mail: String): UserDTO {
        logger.info { "Try to find user by mail : $mail " }

        return userRepository.findByEmail(mail)
            ?.toDto()
            ?: throw RHException("User not found with email: $mail")
    }

    fun getUserById(userId: Long): User {
        logger.info { "Try to find user by id : $userId " }

        return userRepository.findByUserId(userId)
            ?: throw RHException("User not found with id: $userId")
    }

    fun updateUser(userDTO: UserDTO): UserDTO {
        logger.info { "Update user : ${userDTO.email.maskEmail()} " }

        return userRepository.findByUserId(userDTO.id)
            ?.let { user ->
                val updatedUser = user.copy(
                    firstName = userDTO.firstName,
                    lastName = userDTO.lastName,
                    email = userDTO.email,
                    urlPhoto = userDTO.urlPhoto
                )

                userRepository.save(updatedUser).toDto()
            } ?: throw RHException("User not found with id: ${userDTO.id}")
    }

    fun deActivateUser(userId: Long) {
        logger.info { "Deactivating user with id: $userId" }

        userRepository.findByUserId(userId)
            ?.let { user ->
                user.isActive = false
                userRepository.save(user).toDto()
            } ?: throw RHException("User not found with id: $userId")
    }

    fun activateUser(userId: Long) {
        logger.info { "Activating user with id: $userId" }

        userRepository.findByUserId(userId)
            ?.let { user ->
                user.isActive = true
                userRepository.save(user).toDto()
            } ?: throw RHException("User not found with id: $userId")
    }
    
    fun findOrCreateUserFromOAuth(googleUserInfo: GoogleUserInfo, provider: OAuthProvider): User {
        logger.info { "Finding or creating user from ${provider.displayName}: ${googleUserInfo.email.maskEmail()}" }
        
        // Try to find existing user by email
        val existingUser = userRepository.findByEmail(googleUserInfo.email)
        
        return if (existingUser != null) {
            // Update existing user with latest OAuth info
            val updatedUser = existingUser.copy(
                firstName = googleUserInfo.givenName ?: existingUser.firstName,
                lastName = googleUserInfo.familyName ?: existingUser.lastName,
                urlPhoto = googleUserInfo.picture ?: existingUser.urlPhoto,
                isActive = true // Reactivate if user was deactivated
            )
            userRepository.save(updatedUser)
        } else {
            // Create new user from OAuth info
            val newUser = User(
                userId = 0, // Will be auto-generated
                firstName = googleUserInfo.givenName ?: "",
                lastName = googleUserInfo.familyName ?: "",
                email = googleUserInfo.email,
                pushNotificationToken = "", // Will be set by client later
                urlPhoto = googleUserInfo.picture,
                isActive = true
            )
            userRepository.save(newUser)
        }
    }
    
    // Keep the old method for backward compatibility
    fun findOrCreateUserFromGoogle(googleUserInfo: GoogleUserInfo): User {
        return findOrCreateUserFromOAuth(googleUserInfo, OAuthProvider.GOOGLE)
    }
}
