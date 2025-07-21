package com.yb.rh.services

import com.yb.rh.dtos.CarUsersDTO
import com.yb.rh.dtos.UserCarDTO
import com.yb.rh.dtos.UserCarsDTO
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UserCar
import com.yb.rh.error.RHException
import com.yb.rh.repositories.UserCarRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class UserCarService(
    private val userCarRepository: UserCarRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun createUserCar(user: User, car: Car): UserCarsDTO {
        logger.info { "Create UserCar for User ID : ${user.userId} and Car ID : ${car.id}" }

        userCarRepository.save(UserCar(user, car))

        logger.info { "Successfully created UserCar for User ID : ${user.userId} and Car ID : ${car.id}" }

        return getUserCarsByUser(user)
    }

    fun getUsersCarsByUser(user: User): List<UserCarDTO> {
        logger.info { "Try to find UsersCars for User ID : ${user.userId}" }

        return userCarRepository.findAllByUser(user)
            .map { userCar -> userCar.toDto() }
            .also { usersCars ->
                logger.info { "Found ${usersCars.size} UserCars for User ID : ${user.userId}" }
            }
    }

    fun getUsersCarsByCar(car: Car): List<UserCarDTO> {
        logger.info { "Try to find UsersCars for Car ID : ${car.id}" }

        return userCarRepository.findAllByCar(car).map { userCar ->
            userCar.toDto()
        }.also { usersCars ->
            logger.info { "Found ${usersCars.size} UserCars for Car ID : ${car.id}" }
        }
    }

    fun getUserCarByUserAndCar(user: User, car: Car): UserCarDTO? {
        logger.info { "Try to find UserCar for User ID : ${user.userId} and Car ID : ${car.id}" }

        return userCarRepository.findByUserAndCar(user, car)
            ?.toDto()
            ?: throw RHException("UserCar not found for User ID : ${user.userId} and Car ID : ${car.id}")
    }

    fun getUserCarsByUser(user: User): UserCarsDTO {
        logger.info { "Fetching UserCars for User ID: ${user.userId}" }

        return UserCarsDTO(user.toDto(), getUsersCarsByUser(user).map { it.car })
    }

    fun getCarUsersByCar(car: Car): CarUsersDTO {
        logger.info { "Fetching UserCars for Car ID : ${car.id}" }

        return CarUsersDTO(car.toDto(), getUsersCarsByCar(car).map { it.user })
    }

    fun deleteUserCar(user: User, car: Car): UserCarsDTO {
        logger.info { "Deleting UserCar for User ID : ${user.userId} and Car ID : ${car.id}" }

        val userCar = userCarRepository.findByUserAndCar(user, car)
            ?: throw RHException("UserCar not found for User ID : ${user.userId} and Car ID : ${car.id}")

        userCarRepository.delete(userCar)

        logger.info { "Successfully deleted UserCar for User ID : ${user.userId} and Car ID : ${car.id}" }

        return getUserCarsByUser(user)
    }
}






