package com.yb.rh.services

import com.yb.rh.common.NotificationsKind
import com.yb.rh.dtos.*
import com.yb.rh.entities.Car
import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    fun createUserCar(userCarRequestDTO: UserCarRequestDTO): UserCarsDTO {
        logger.info { "Creating UserCar with User ID: ${userCarRequestDTO.userId} and Car ID: ${userCarRequestDTO.carId}" }

        val user = userService.getUserById(userCarRequestDTO.userId)
        val car = carService.getCarById(userCarRequestDTO.carId)

        return userCarService.createUserCar(user, car)
            .also {
                logger.info { "Successfully created UserCar for User ID: ${user.userId} and Car ID: ${car.id}" }
            }
    }

    @Transactional
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

    @Transactional
    fun createCarsRelations(carsRelationRequestDTO: CarsRelationRequestDTO): CarRelationsDTO {
        logger.info { "Creating Cars Relation between Blocking Car: ${carsRelationRequestDTO.blockingCarId} and Blocked Car: ${carsRelationRequestDTO.blockedCarId}" }

        val blockingCar = carService.getCarById(carsRelationRequestDTO.blockingCarId)
        val blockedCar = carService.getCarById(carsRelationRequestDTO.blockedCarId)

        // Validate that cars are different
        if (blockingCar.id == blockedCar.id) {
            throw IllegalArgumentException("A car cannot block itself")
        }

        // Check for circular blocking
        if (carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar)) {
            throw IllegalArgumentException("Creating this blocking relationship would create a circular dependency")
        }

        carsRelationsService.createCarsRelation(blockingCar, blockedCar)

        logger.info { "Successfully created Cars Relation between Blocking Car: ${blockingCar.plateNumber} and Blocked Car: ${blockedCar.plateNumber}" }

        // Send appropriate notifications based on the user situation
        var notificationMessage: String?
        when (carsRelationRequestDTO.userCarSituation) {
            UserCarSituation.IS_BLOCKING -> {
                // User's car is blocking another car - notify blocked car's owners
                val blockedCarUsers = userCarService.getCarUsersByCar(blockedCar)
                if (blockedCarUsers.users.isNotEmpty()) {
                    sendBlockedNotification(blockedCar)
                    notificationMessage = "Blocking relationship created. Notifications sent to owner(s) of car ${blockedCar.plateNumber}."
                } else {
                    logger.warn { "Car ${blockedCar.id} (${blockedCar.plateNumber}) has no owner, skipping notification" }
                    notificationMessage = "Blocking relationship created. No notifications sent - car ${blockedCar.plateNumber} has no registered owners."
                }
            }

            UserCarSituation.IS_BLOCKED -> {
                // User's car is being blocked - notify blocking car's owners
                val blockingCarUsers = userCarService.getCarUsersByCar(blockingCar)
                if (blockingCarUsers.users.isNotEmpty()) {
                    sendBlockingNotification(blockingCar)
                    notificationMessage = "Blocking relationship created. Notifications sent to owner(s) of car ${blockingCar.plateNumber}."
                } else {
                    logger.warn { "Car ${blockingCar.id} (${blockingCar.plateNumber}) has no owner, skipping notification" }
                    notificationMessage = "Blocking relationship created. No notifications sent - car ${blockingCar.plateNumber} has no registered owners."
                }
            }
        }

        val userCar = getActualUserCar(carsRelationRequestDTO.userCarSituation, blockingCar, blockedCar)

        return carsRelationsService.findCarRelationsDTO(userCar, notificationMessage)
    }

    fun getCarRelationsByCarId(carId: Long): CarRelationsDTO {
        logger.info { "Fetching Car Relations for Car ID: $carId" }

        val car = carService.getCarById(carId)

        return carsRelationsService.findCarRelationsDTO(car)
            .also { logger.info { "Found Car Relations for Car ID: $carId - $it" } }
    }

    @Transactional
    fun deleteCarsRelations(carsRelationRequestDTO: CarsRelationRequestDTO): CarRelationsDTO {
        logger.info { "Deleting Cars Relation between Blocking Car: ${carsRelationRequestDTO.blockingCarId} and Blocked Car: ${carsRelationRequestDTO.blockedCarId}" }

        val blockingCar = carService.getCarById(carsRelationRequestDTO.blockingCarId)
        val blockedCar = carService.getCarById(carsRelationRequestDTO.blockedCarId)

        carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar)

        logger.info { "Successfully deleted Cars Relation between Blocking Car: ${blockingCar.plateNumber} and Blocked Car: ${blockedCar.plateNumber}" }

        // Send "free to go" notification to blocked car's owners
        val blockedCarUsers = userCarService.getCarUsersByCar(blockedCar)
        val notificationMessage = if (blockedCarUsers.users.isNotEmpty()) {
            sendFreeToGoNotification(blockedCar)
            "Blocking relationship removed. Notifications sent to owner(s) of car ${blockedCar.plateNumber}."
        } else {
            logger.warn { "Car ${blockedCar.id} (${blockedCar.plateNumber}) has no owner, skipping notification" }
            "Blocking relationship removed. No notifications sent - car ${blockedCar.plateNumber} has no registered owners."
        }

        val userCar = getActualUserCar(carsRelationRequestDTO.userCarSituation, blockingCar, blockedCar)

        return carsRelationsService.findCarRelationsDTO(userCar, notificationMessage)
    }

    @Transactional
    fun deleteAllCarRelationsByCarId(carId: Long) {
        logger.info { "Deleting all Cars Relations for Car ID: $carId" }

        val car = carService.getCarById(carId)

        // Get all cars that were being blocked by this car before deletion
        val blockedCars = carsRelationsService.findCarRelations(car).isBlocking

        carsRelationsService.deleteAllCarsRelations(car)

        logger.info { "Successfully deleted all Cars Relations for Car ID: $carId" }

        // Send "free to go" notifications to all previously blocked cars
        blockedCars.forEach { blockedCar ->
            val blockedCarUsers = userCarService.getCarUsersByCar(blockedCar)
            if (blockedCarUsers.users.isNotEmpty()) {
            sendFreeToGoNotification(blockedCar)
            } else {
                logger.warn { "Car ${blockedCar.id} (${blockedCar.plateNumber}) has no owner, skipping notification" }
            }
        }
    }

    internal fun getActualUserCar(userCarSituation: UserCarSituation, blockingCar: Car, blockedCar: Car): Car {
        return when (userCarSituation) {
            UserCarSituation.IS_BLOCKED -> blockedCar
            UserCarSituation.IS_BLOCKING -> blockingCar
        }
    }

    /* Notifications Management */

    fun sendNeedToGoNotification(blockedCarId: Long): String {
        val blockedCar = carService.getCarById(blockedCarId)
        val carRelations = carsRelationsService.findCarRelations(blockedCar)

        if (carRelations.isBlockedBy.isEmpty()) {
            logger.info { "Car $blockedCarId is not blocked by any other car, no notifications needed" }
            throw RHException("Car is not blocked by any other car")
        }

        // Check if the blocked car has an owner
        val blockedCarUsers = userCarService.getCarUsersByCar(blockedCar)
        if (blockedCarUsers.users.isEmpty()) {
            logger.warn { "Car $blockedCarId (${blockedCar.plateNumber}) has no owner, cannot send need to go notification" }
            throw RHException(
                "This car has no user so no one would be notified. Consider finding the owner and telling them to use this app.",
                ErrorType.CAR_HAS_NO_OWNER
            )
        }

        sendNeedToGoNotification(blockedCar, mutableSetOf())
        return "Notification sent successfully"
    }

    internal fun sendNeedToGoNotification(blockedCar: Car, visitedCars: MutableSet<Long>) {
        logger.info { "Blocked Car ID: ${blockedCar.id} needs to go! Sending notification to all blocking cars" }

        // Prevent infinite recursion by tracking visited cars
        if (visitedCars.contains(blockedCar.id)) {
            logger.warn { "Circular blocking detected for car ${blockedCar.id}, stopping notification chain" }
            return
        }
        visitedCars.add(blockedCar.id)

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

            // Continue the chain but with visited tracking
            sendNeedToGoNotification(blockingCar, visitedCars)
        }
    }

    internal fun sendBlockedNotification(blockedCar: Car) {
        logger.info { "Sending blocked notification to owners of car ${blockedCar.id}" }

        val carUsersDTO = userCarService.getCarUsersByCar(blockedCar)
        carUsersDTO.users.forEach { userCar ->
            val user = userService.getUserById(userCar.id)
            notificationService.sendPushNotification(user, NotificationsKind.BEEN_BLOCKED)
        }
    }

    internal fun sendBlockingNotification(blockingCar: Car) {
        logger.info { "Sending blocking notification to owners of car ${blockingCar.id}" }

        val carUsersDTO = userCarService.getCarUsersByCar(blockingCar)
        carUsersDTO.users.forEach { userCar ->
            val user = userService.getUserById(userCar.id)
            notificationService.sendPushNotification(user, NotificationsKind.BEEN_BLOCKING)
        }
    }

    internal fun sendFreeToGoNotification(freedCar: Car) {
        logger.info { "Sending free to go notification to owners of car ${freedCar.id}" }

        val carUsersDTO = userCarService.getCarUsersByCar(freedCar)
        carUsersDTO.users.forEach { userCar ->
            val user = userService.getUserById(userCar.id)
            notificationService.sendPushNotification(user, NotificationsKind.FREE_TO_GO)
        }
    }
}
