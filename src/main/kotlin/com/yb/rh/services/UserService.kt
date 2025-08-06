package com.yb.rh.services

import com.yb.rh.dtos.UserDTO
import com.yb.rh.entities.User
import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import com.yb.rh.repositories.UserRepository
import com.yb.rh.utils.maskEmail
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class UserService(
    private val userRepository: UserRepository,
    private val currentUserService: CurrentUserService
) {
    private val logger = KotlinLogging.logger {}

    fun getUserDTOByToken(): UserDTO {
        logger.info { "Getting current user from token" }
        val currentUser = currentUserService.getCurrentUser()
        return currentUser.toDto()
    }

    fun getUserById(userId: Long): User {
        logger.info { "Try to find user by id : $userId " }

        return userRepository.findByUserId(userId)
            ?: throw RHException("User not found with id: $userId")
    }

    fun getUserByEmail(email: String): User {
        logger.info { "Try to find user by email : ${email.maskEmail()} " }

        return userRepository.findByEmail(email)
            ?: throw RHException("User not found with email: ${email.maskEmail()}")
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

    fun deActivateUser() {
        currentUserService.getCurrentUser()
            .let { user ->
                logger.info { "Deactivating user with id: ${user.userId}" }
                user.isActive = false
                userRepository.save(user).toDto()
            }
    }

    fun activateUser() {
        currentUserService.getCurrentUser().let { user ->
            logger.info { "Activating user with id: ${user.userId}" }
            user.isActive = true
            userRepository.save(user).toDto()
        }
    }

    fun findOrCreateUserFromOAuth(userDTO: UserDTO, agreedConsent: Boolean?): User {
        logger.debug("Starting OAuth user creation/lookup for email: ${userDTO.email.maskEmail()}")
        
        val existingUser = userRepository.findByEmail(userDTO.email)
        
        return if (existingUser != null) {
            logger.debug("Found existing user: ${existingUser.email.maskEmail()}, updating if needed")
            val updatedUser = existingUser.copy(
                firstName = userDTO.firstName,
                lastName = userDTO.lastName,
                urlPhoto = userDTO.urlPhoto ?: existingUser.urlPhoto,
            )
            val savedUser = userRepository.save(updatedUser)
            logger.debug("Updated existing user: ${savedUser.email.maskEmail()}")
            savedUser
        } else {
            logger.debug("Creating new user with email: ${userDTO.email.maskEmail()}")
            if (agreedConsent == null || !agreedConsent) {
                logger.warn("User consent not provided for email: ${userDTO.email.maskEmail()}")
                throw RHException("User consent is required for OAuth login", ErrorType.USER_CONSENT_REQUIRED)
            }
            val newUser = User(
                userId = 0,
                firstName = userDTO.firstName,
                lastName = userDTO.lastName,
                email = userDTO.email,
                pushNotificationToken = "",
                urlPhoto = userDTO.urlPhoto,
            )
            val savedUser = userRepository.save(newUser)
            logger.debug("Created new user: ${savedUser.email.maskEmail()}")
            savedUser
        }
    }
}
