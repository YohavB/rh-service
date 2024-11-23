package com.yb.rh.services

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onFailure
import com.yb.rh.entities.CarDTO
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.error.RHException
import com.yb.rh.repositories.*
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class UsersService(
    private val repository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<User> = repository.findAll().toList()

    fun findByUserId(userId: Long): Result<UserDTO, RHException> {
        logger.info { "Try to find user : $userId " }
        return repository.findByUserIdSafe(userId)
            .onFailure { logger.warn(it) { "Failed" } }
            .andThen { insertCarDtoListToUserDto(it) }
    }

    fun findByEmail(mail: String): Result<UserDTO, RHException> {
        logger.info { "Try to find user by mail : $mail " }
        return repository.findByEmailSafe(mail)
            .onFailure { logger.warn(it) { "Failed" } }
            .andThen { insertCarDtoListToUserDto(it) }
    }

    fun createOrUpdateUser(userDTO: UserDTO): Result<UserDTO, RHException> {
        logger.info { "Create or update user : ${userDTO.email} " }
        return repository.saveSafe(userDTO.toEntity())
            .onFailure { logger.warn(it) { "Failed" } }
            .map { it.toDto() }
    }

    private fun insertCarDtoListToUserDto(user: User): Result<UserDTO, RHException> {
        return usersCarsRepository.findByUserSafe(user)
            .onFailure { logger.warn(it) { "failed" } }
            .map {
                val userCars: List<CarDTO> = it.map { userCar -> userCar.car.toDto() }
                user.toDto(userCars)
            }
    }
}
