package com.yb.rh.services

import com.yb.rh.dtos.CarRelations
import com.yb.rh.dtos.CarRelationsDTO
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsRelations
import com.yb.rh.repositories.CarsRelationsRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class CarsRelationsService(
    private val carsRelationsRepository: CarsRelationsRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun createCarsRelation(blockingCar: Car, blockedCar: Car) {
        logger.info { "Create relation between Car : ${blockingCar.plateNumber} blocking Car : ${blockedCar.plateNumber}" }

        carsRelationsRepository.save(CarsRelations(blockingCar = blockingCar, blockedCar = blockedCar))

        logger.info { "Successfully created Cars Relation for Car : ${blockingCar.plateNumber} and Car : ${blockedCar.plateNumber}" }
    }

    fun findCarRelations(car: Car): CarRelations {
        logger.info { "Finding Car Relations for Car : ${car.plateNumber}" }

        val isBlocking = carsRelationsRepository.findByBlockingCar(car)
        val isBlockedBy = carsRelationsRepository.findByBlockedCar(car)
        val carRelations = CarRelations(
            car = car,
            isBlocking = isBlocking.map { it.blockedCar },
            isBlockedBy = isBlockedBy.map { it.blockingCar }
        )

        logger.info { "Found Car Relations for Car : ${car.plateNumber} - $carRelations" }

        return carRelations
    }

    fun findCarRelationsDTO(car: Car): CarRelationsDTO {
        logger.info { "Finding Car Relations for Car : ${car.plateNumber}" }

        val carRelations = findCarRelations(car)

        return CarRelationsDTO(
            car = car.toDto(),
            isBlocking = carRelations.isBlocking.map { it.toDto() },
            isBlockedBy = carRelations.isBlockedBy.map { it.toDto() }
        )
    }

    fun deleteSpecificCarsRelation(blockingCar: Car, blockedCar: Car) {
        logger.info { "Deleting relation between Car : ${blockingCar.plateNumber} blocking Car : ${blockedCar.plateNumber}" }

        carsRelationsRepository.findByBlockingCar(blockingCar)
            .filter { it.blockedCar == blockedCar }
            .forEach { carsRelationsRepository.delete(it) }

        logger.info { "Successfully deleted Cars Relations for Car : ${blockingCar.plateNumber} and Car : ${blockedCar.plateNumber}" }
    }

    fun deleteAllCarsRelations(car: Car) {
        logger.info { "Deleting all relations for Car : ${car.plateNumber}" }

        carsRelationsRepository.findByBlockingCar(car).forEach { carsRelationsRepository.delete(it) }
        carsRelationsRepository.findByBlockedCar(car).forEach { carsRelationsRepository.delete(it) }

        logger.info { "Successfully deleted all Cars Relations for Car : ${car.plateNumber}" }
    }
}



