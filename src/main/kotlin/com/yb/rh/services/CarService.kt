package com.yb.rh.services

import com.yb.rh.common.Countries
import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.FindCarRequestDTO
import com.yb.rh.entities.Car
import com.yb.rh.repositories.CarRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CarService(
    private val carRepository: CarRepository,
    private val carApi: CarApi
) {
    private val logger = KotlinLogging.logger {}

    fun getCarOrCreateRequest(requestDTO: FindCarRequestDTO): CarDTO {
        logger.info { "Try to find Car : ${requestDTO.plateNumber}" }

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

        return carRepository.findByPlateNumber(plateNumber)
            ?: createCar(plateNumber, country)
                .also { logger.info { "Created new Car with plate number: ${it.plateNumber}" } }
    }

    fun getCarById(carId: Long): Car {
        logger.info { "Getting Car by ID: $carId" }
        return carRepository.findCarById(carId)
            ?: throw IllegalArgumentException("Car with ID $carId not found")
    }

    private fun createCar(plateNumber: String, country: Countries): Car {
        logger.info { "Creating new $country Car with plate number: $plateNumber" }

        return carApi.getCarInfo(plateNumber, country)
            .let { carDTO -> carRepository.save(Car.fromDto(carDTO)) }
    }
}
