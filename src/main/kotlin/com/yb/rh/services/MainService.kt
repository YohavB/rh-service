package com.yb.rh.services

import com.yb.rh.common.NotificationsKind
import com.yb.rh.dtos.*
import com.yb.rh.entities.Car
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class MainService(
    private val userService: UserService,
    private val carService: CarService,
    private val userCarService: UserCarService,
    private val carsRelationsService: CarsRelationsService,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    /* Users - Cars Management */

    fun createUserCar(userCarRequestDTO: UserCarRequestDTO): UserCarsDTO {
        logger.info { "Creating UserCar with User ID: ${userCarRequestDTO.userId} and Car ID: ${userCarRequestDTO.carId}" }

        val user = userService.getUserById(userCarRequestDTO.userId)
        val car = carService.getCarById(userCarRequestDTO.carId)

        return userCarService.createUserCar(user, car)
            .also {
                logger.info { "Successfully created UserCar for User ID: ${user.userId} and Car ID: ${car.id}" }
            }
    }

    fun deleteUserCar(userCarRequestDTO: UserCarRequestDTO): UserCarsDTO {
        logger.info { "Deleting UserCar with User ID: ${userCarRequestDTO.userId} and Car ID: ${userCarRequestDTO.carId}" }

        val user = userService.getUserById(userCarRequestDTO.userId)
        val car = carService.getCarById(userCarRequestDTO.carId)

        return userCarService.getUserCarsByUser(user)
            .also {
                userCarService.getUserCarByUserAndCar(user, car)
                userCarService.deleteUserCar(user, car)
                logger.info { "Successfully deleted UserCar for User ID: ${user.userId} and Car ID: ${car.id}" }
            }
    }

    fun getUserCarsByUser(userId: Long): UserCarsDTO {
        logger.info { "Fetching UserCars for User ID: $userId" }

        val user = userService.getUserById(userId)

        return userCarService.getUserCarsByUser(user)
            .also { logger.info { "Found ${it.cars.size} UserCars for User ID: $userId" } }
    }

    /* Cars Relations Management */

    fun createCarsRelations(carsRelationRequestDTO: CarsRelationRequestDTO): CarRelationsDTO {
        logger.info { "Creating Cars Relation between Blocking Car: ${carsRelationRequestDTO.blockingCarId} and Blocked Car: ${carsRelationRequestDTO.blockedCarId}" }

        val blockingCar = carService.getCarById(carsRelationRequestDTO.blockingCarId)
        val blockedCar = carService.getCarById(carsRelationRequestDTO.blockedCarId)

        carsRelationsService.createCarsRelation(blockingCar, blockedCar)

        logger.info { "Successfully created Cars Relation between Blocking Car: ${blockingCar.plateNumber} and Blocked Car: ${blockedCar.plateNumber}" }

        //sending blocked notification to blocked cars if user situation is IS_BLOCKING
        //sending blocking notification to blocking cars if user situation is IS_BLOCKED

        val userCar = getActualUserCar(carsRelationRequestDTO.userCarSituation, blockingCar, blockedCar)

        return carsRelationsService.findCarRelationsDTO(userCar)
    }

    fun getCarRelationsByCarId(carId: Long): CarRelationsDTO {
        logger.info { "Fetching Car Relations for Car ID: $carId" }

        val car = carService.getCarById(carId)

        return carsRelationsService.findCarRelationsDTO(car)
            .also { logger.info { "Found Car Relations for Car ID: $carId - $it" } }
    }

    fun deleteCarsRelations(carsRelationRequestDTO: CarsRelationRequestDTO): CarRelationsDTO {
        logger.info { "Deleting Cars Relation between Blocking Car: ${carsRelationRequestDTO.blockingCarId} and Blocked Car: ${carsRelationRequestDTO.blockedCarId}" }

        val blockingCar = carService.getCarById(carsRelationRequestDTO.blockingCarId)
        val blockedCar = carService.getCarById(carsRelationRequestDTO.blockedCarId)

        //sending free to go notification to blocked cars if user situation is IS_BLOCKING

        carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar)

        logger.info { "Successfully deleted Cars Relation between Blocking Car: ${blockingCar.plateNumber} and Blocked Car: ${blockedCar.plateNumber}" }

        val userCar = getActualUserCar(carsRelationRequestDTO.userCarSituation, blockingCar, blockedCar)

        return carsRelationsService.findCarRelationsDTO(userCar)
    }

    fun deleteAllCarRelationsByCarId(carId: Long) {
        logger.info { "Deleting all Cars Relations for Car ID: $carId" }

        val car = carService.getCarById(carId)

        //sending free to go notification to all blocked cars

        carsRelationsService.deleteAllCarsRelations(car)

        logger.info { "Successfully deleted all Cars Relations for Car ID: $carId" }

    }

    private fun getActualUserCar(userCarSituation: UserCarSituation, blockingCar: Car, blockedCar: Car): Car {
        return when (userCarSituation) {
            UserCarSituation.IS_BLOCKED -> blockedCar
            UserCarSituation.IS_BLOCKING -> blockingCar
        }
    }

    /* Notifications Management */

    fun sendNeedToGoNotification(blockedCarId: Long) {
        val blockedCar = carService.getCarById(blockedCarId)
        sendNeedToGoNotification(blockedCar)
    }

    private fun sendNeedToGoNotification(blockedCar: Car) {
        logger.info { "Blocked Car ID: ${blockedCar.id} need to go! Sending notification to all blocking car" }

        val carRelations = carsRelationsService.findCarRelations(blockedCar)

        if (carRelations.isBlockedBy.isEmpty()) {
            logger.info { "No blocking cars found for blocked car ID: ${blockedCar.id}" }
            return
        }

        carRelations.isBlockedBy.forEach { blockingCar ->

            val carUsersDTO = userCarService.getCarUsersByCar(blockingCar)

            carUsersDTO.users.forEach { userCar ->
                logger.info { "User ${userCar.id} has car ${blockingCar.id} which is blocking ${blockedCar.id}, sending notification" }
                val blockingUser = userService.getUserById(userCar.id)
                notificationService.sendPushNotification(blockingUser, NotificationsKind.NEED_TO_GO)
            }

            sendNeedToGoNotification(blockingCar)
        }
    }
}
