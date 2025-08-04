package com.yb.rh.services

import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.FindCarRequestDTO
import com.yb.rh.entities.Car
import com.yb.rh.enum.Countries
import com.yb.rh.error.RHException
import com.yb.rh.repositories.CarRepository
import jakarta.validation.constraints.NotBlank
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarService(
    private val carRepository: CarRepository,
    private val carApi: CarApi
) {
    private val logger = KotlinLogging.logger {}

    fun getCarOrCreateRequest(requestDTO: FindCarRequestDTO): CarDTO {
        logger.info { "Try to find Car : ${requestDTO.plateNumber}" }

        validatePlateNumber(requestDTO.plateNumber)

        return carRepository.findByPlateNumber(requestDTO.plateNumber)
            ?.let {
                logger.info { "Found Car: ${it.plateNumber} in our records" }
                it.toDto()
            }
            ?: createCar(requestDTO.plateNumber, requestDTO.country)
                .toDto()
                .also { logger.info { "Created Car: ${it.plateNumber} in our records" } }
    }

    fun getCarOrCreate(plateNumber: String, country: Countries): Car {
        logger.info { "Fetching Car with plate number: $plateNumber" }

        validatePlateNumber(plateNumber)

        return carRepository.findByPlateNumber(plateNumber)
            ?: createCar(plateNumber, country)
                .also { logger.info { "Created new Car with plate number: ${it.plateNumber}" } }
    }

    fun getCarById(carId: Long): Car {
        logger.info { "Getting Car by ID: $carId" }
        return carRepository.findCarById(carId)
            ?: throw RHException("Car with ID $carId not found")
    }

    @Transactional
    internal fun createCar(plateNumber: String, country: Countries): Car {
        logger.info { "Creating new $country Car with plate number: $plateNumber" }

        return try {
            carApi.getCarInfo(plateNumber, country)
                .let { carDTO -> carRepository.save(Car.fromDto(carDTO)) }
        } catch (e: Exception) {
            logger.error(e) { "Failed to create car with plate number: $plateNumber" }
            throw RHException("Failed to create car with plate number: $plateNumber. ${e.message}")
        }
    }

    internal fun validatePlateNumber(@NotBlank plateNumber: String) {
        if (plateNumber.isBlank()) {
            throw RHException("Plate number cannot be blank")
        }
        
        if (plateNumber.length > 50) {
            throw RHException("Plate number cannot exceed 50 characters")
        }
    }
}
