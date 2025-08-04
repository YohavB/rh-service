package com.yb.rh.services

import com.yb.rh.dtos.UserDTO
import com.yb.rh.entities.User
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

    fun findOrCreateUserFromOAuth(userDTO: UserDTO): User {
        logger.debug("Starting OAuth user creation/lookup for email: ${userDTO.email.maskEmail()}")
        
        val existingUser = userRepository.findByEmail(userDTO.email)
        
        return if (existingUser != null) {
            logger.debug("Found existing user: ${existingUser.email.maskEmail()}, updating if needed")
            val updatedUser = existingUser.copy(
                firstName = userDTO.firstName,
                lastName = userDTO.lastName,
                urlPhoto = userDTO.urlPhoto ?: existingUser.urlPhoto,
                isActive = true
            )
            val savedUser = userRepository.save(updatedUser)
            logger.debug("Updated existing user: ${savedUser.email.maskEmail()}")
            savedUser
        } else {
            logger.debug("Creating new user with email: ${userDTO.email.maskEmail()}")
            val newUser = User(
                userId = 0,
                firstName = userDTO.firstName,
                lastName = userDTO.lastName,
                email = userDTO.email,
                pushNotificationToken = "",
                urlPhoto = userDTO.urlPhoto,
                isActive = true
            )
            val savedUser = userRepository.save(newUser)
            logger.debug("Created new user: ${savedUser.email.maskEmail()}")
            savedUser
        }
    }
}
