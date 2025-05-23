package com.yb.rh.services

import com.github.michaelbull.result.*
import com.google.gson.GsonBuilder
import com.yb.rh.common.CarStatus
import com.yb.rh.common.Countries
import com.yb.rh.common.UserStatus
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import com.yb.rh.entities.UsersCars
import com.yb.rh.error.RHException
import com.yb.rh.repositories.*
import com.yb.rh.services.countryCarJson.CountryCarJson
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class CarService(
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository,
    private val carApiInterface: CarApiInterface
) {

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Car> = carsRepository.findAll().toList()

    fun findByPlateNumber(plateNumber: String, country: Countries): Result<CarDTO, RHException> {
        logger.info { "Try to find Car : $plateNumber" }
        return carsRepository.findByPlateNumberSafe(plateNumber)
            .map { it.toDto() }
            .onFailure { e ->
                logger.warn(e) { "Failed" }
                getCarInfo(plateNumber, country)
                    .onFailure { logger.warn(it) { "Failed" } }
                    .andThen { carDTO ->
                        carsRepository.saveSafe(Car.fromDto(carDTO))
                            .onFailure { logger.warn(it) { "Failed" } }
                            .map { car -> car.toDto() }
                    }
            }
    }


    fun createOrUpdateCar(
        plateNumber: String,
        country: Countries,
        userId: Long?,
        carStatus: CarStatus? = null
    ): Result<Car, RHException> {
        logger.info { "Try to create or update Car : $plateNumber of user : $userId " }
        return getCarInfo(plateNumber, country)
            .onFailure { logger.warn(it) { "Failed" } }
            .map { carDTO -> carDTO.toEntity() }
            .onSuccess { car ->
                if (carStatus == CarStatus.BLOCKED) car.beingBlocked()
                if (carStatus == CarStatus.BLOCKING) car.beingBlocking()
            }
            .onSuccess { carsRepository.saveSafe(it) }
            .onFailure { logger.warn(it) { "Failed" } }
            .onSuccess { currentCar ->
                userId?.let { userId ->
                    usersRepository.findByUserIdSafe(userId)
                        .onSuccess { currentUser ->
                            usersCarsRepository.saveSafe(UsersCars(currentUser, currentCar))
                                .onFailure { logger.warn(it) { "Failed" } }
                        }
                        .onFailure { logger.warn(it) { "Failed" } }
                }
            }
    }

    private fun getCarInfo(plateNumber: String, country: Countries): Result<CarDTO, RHException> {
        logger.info { "Try to get Car Info $plateNumber from $country" }

        return Ok(carApiInterface.getCarInfo(plateNumber, country))
    }

    fun findAndUpdateCar(
        carPlate: String,
        userId: Long,
        userStatus: UserStatus,
        block: Boolean,
        carStatus: CarStatus,
    ): Result<Car, RHException> {
        return carsRepository.findByPlateNumberSafe(carPlate)
            .onSuccess { car ->
                if (block) car.beingBlocked() else car.unblocked()
                carsRepository.saveSafe(car)
                    .onFailure { logger.warn(it) { "Failed to saved Car : $carPlate after updating his status" } }
                    .onSuccess { logger.info { "Successfully saved Car : $carPlate after updating his status" } }
            }
            .onFailure {
                createOrUpdateCar(
                    carPlate,
                    Countries.IL,
                    if (userStatus.name == carStatus.name) userId else null,
                    carStatus
                )
                    .onFailure { logger.warn(it) { "Failed to create Car : $carPlate after updating his status" } }
                    .onSuccess { logger.info { "Successfully create Car : $carPlate after updating his status" } }
            }
    }
}
