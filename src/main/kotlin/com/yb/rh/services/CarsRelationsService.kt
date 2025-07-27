package com.yb.rh.services

import com.yb.rh.dtos.CarRelations
import com.yb.rh.dtos.CarRelationsDTO
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsRelations
import com.yb.rh.error.RHException
import com.yb.rh.repositories.CarsRelationsRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CarsRelationsService(
    private val carsRelationsRepository: CarsRelationsRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createCarsRelation(blockingCar: Car, blockedCar: Car) {
        logger.info { "Create relation between Car : ${blockingCar.plateNumber} blocking Car : ${blockedCar.plateNumber}" }

        // Check if relation already exists
        val existingRelation = carsRelationsRepository.findByBlockingCar(blockingCar)
            .find { it.blockedCar.id == blockedCar.id }

        if (existingRelation != null) {
            throw RHException("Blocking relationship already exists between ${blockingCar.plateNumber} and ${blockedCar.plateNumber}")
        }

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

    fun findCarRelationsDTO(car: Car, message: String? = null): CarRelationsDTO {
        logger.info { "Finding Car Relations for Car : ${car.plateNumber}" }

        val carRelations = findCarRelations(car)

        return CarRelationsDTO(
            car = car.toDto(),
            isBlocking = carRelations.isBlocking.map { it.toDto() },
            isBlockedBy = carRelations.isBlockedBy.map { it.toDto() },
            message = message
        )
    }

    @Transactional
    fun deleteSpecificCarsRelation(blockingCar: Car, blockedCar: Car) {
        logger.info { "Deleting relation between Car : ${blockingCar.plateNumber} blocking Car : ${blockedCar.plateNumber}" }

        val relationsToDelete = carsRelationsRepository.findByBlockingCar(blockingCar)
            .filter { it.blockedCar.id == blockedCar.id }

        if (relationsToDelete.isEmpty()) {
            throw RHException("No blocking relationship found between ${blockingCar.plateNumber} and ${blockedCar.plateNumber}")
        }

        relationsToDelete.forEach { carsRelationsRepository.delete(it) }

        logger.info { "Successfully deleted Cars Relations for Car : ${blockingCar.plateNumber} and Car : ${blockedCar.plateNumber}" }
    }

    @Transactional
    fun deleteAllCarsRelations(car: Car) {
        logger.info { "Deleting all relations for Car : ${car.plateNumber}" }

        carsRelationsRepository.findByBlockingCar(car).forEach { carsRelationsRepository.delete(it) }
        carsRelationsRepository.findByBlockedCar(car).forEach { carsRelationsRepository.delete(it) }

        logger.info { "Successfully deleted all Cars Relations for Car : ${car.plateNumber}" }
    }

    /**
     * Check if creating a blocking relationship would create a circular dependency
     */
    fun wouldCreateCircularBlocking(blockingCar: Car, blockedCar: Car): Boolean {
        logger.info { "Checking for circular blocking: ${blockingCar.plateNumber} -> ${blockedCar.plateNumber}" }

        val visited = mutableSetOf<Long>()
        return hasPathToCar(blockedCar, blockingCar, visited)
    }

    internal fun hasPathToCar(startCar: Car, targetCar: Car, visited: MutableSet<Long>): Boolean {
        if (startCar.id == targetCar.id) {
            return true
        }

        if (visited.contains(startCar.id)) {
            return false
        }

        visited.add(startCar.id)

        val blockingRelations = carsRelationsRepository.findByBlockingCar(startCar)
        return blockingRelations.any { relation ->
            hasPathToCar(relation.blockedCar, targetCar, visited)
        }
    }
}



