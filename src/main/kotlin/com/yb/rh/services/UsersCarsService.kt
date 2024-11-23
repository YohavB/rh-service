package com.yb.rh.services

import com.github.michaelbull.result.*
import com.yb.rh.common.CarStatus
import com.yb.rh.common.NotificationsKind
import com.yb.rh.common.UserStatus
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import com.yb.rh.entities.UsersCarsDTO
import com.yb.rh.error.RHException
import com.yb.rh.repositories.*
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class UsersCarsService(
    private val usersCarsRepository: UsersCarsRepository,
    private val carsRepository: CarsRepository,
    private val carService: CarService,
    private val usersRepository: UsersRepository,
    private val notificationService: NotificationService,
) {
    private val logger = KotlinLogging.logger {}

    fun getAllUsersCars(): MutableIterable<UsersCars> =
        usersCarsRepository.findAll()

    fun getUsersCarsByPlateNumber(plateNumber: String): Result<List<UsersCarsDTO>, RHException> {
        logger.info { "Try to find UsersCars by plate : $plateNumber" }
        return carsRepository.findByPlateNumberSafe(plateNumber)
            .onFailure { logger.warn(it) { "Failed to find Car : $plateNumber in car repo" } }
            .andThen { car -> usersCarsRepository.findByCarSafe(car) }
            .onFailure { logger.warn(it) { "Failed to find Car : $plateNumber in usersCars repo" } }
            .map { usersCarsList -> usersCarsList.map { it.toDto() } }
    }

    fun getUsersCarsByUserId(userId: Long): Result<List<UsersCarsDTO>, RHException> {
        logger.info { "Try to find UsersCars by userId : $userId" }
        return usersRepository.findByUserIdSafe(userId)
            .onFailure { logger.warn(it) { "Failed to find User : $userId in user repo" } }
            .andThen { user -> usersCarsRepository.findByUserSafe(user) }
            .onFailure { logger.warn(it) { "Failed to find User : $userId in usersCars repo" } }
            .map { usersCarsList -> usersCarsList.map { it.toDto() } }
    }

    fun getUsersCarsByUserAndPlate(userId: Long, plateNumber: String): Result<UsersCarsDTO, RHException> {
        logger.info { "Try to find UsersCars by userId : $userId" }
        return usersRepository.findByUserIdSafe(userId)
            .onFailure { logger.warn(it) { "Failed to find User : $userId in User repo" } }
            .andThen { user ->
                carsRepository.findByPlateNumberSafe(plateNumber)
                    .onFailure { logger.warn(it) { "Failed to find Car : $plateNumber in car repo" } }
                    .andThen { car -> usersCarsRepository.findByUserAndCarSafe(user, car) }
                    .onFailure { logger.warn(it) { "Failed to find User : $userId and Car : $plateNumber in usersCars repo" } }
                    .map { it.toDto() }
            }
    }

    fun getUsersCarsByBlockedPlateNumber(blockedPlateNumber: String): Result<List<UsersCarsDTO>, RHException> {
        logger.info { "Try to find UsersCars by Blocked Car : $blockedPlateNumber" }
        return carsRepository.findByPlateNumberSafe(blockedPlateNumber)
            .onFailure { logger.warn(it) { "Failed to find blocked Car : $blockedPlateNumber in car repo" } }
            .andThen { usersCarsRepository.findByBlockedCarSafe(it) }
            .onFailure { logger.warn(it) { "Failed to find by blocked car : $blockedPlateNumber in usersCars repo" } }
            .map { usersCarsList -> usersCarsList.map { it.toDto() } }
    }

    fun getUsersCarsByBlockingPlateNumber(blockingPlateNumber: String): Result<List<UsersCarsDTO>, RHException> {
        logger.info { "Try to find UsersCars by Blocking Car : $blockingPlateNumber" }
        return carsRepository.findByPlateNumberSafe(blockingPlateNumber)
            .onFailure { logger.warn(it) { "Failed to find blocking Car : $blockingPlateNumber in car repo" } }
            .andThen { usersCarsRepository.findByBlockingCarSafe(it) }
            .onFailure { logger.warn(it) { "Failed to find by blocking car : $blockingPlateNumber in usersCars repo" } }
            .map { usersCarsList -> usersCarsList.map { it.toDto() } }
    }

    fun updateBlockedCar(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long,
        userStatus: UserStatus,
    ): Result<Unit, RHException> {
        logger.info { "Try to update that Car : $blockingCarPlate is blocking Car : $blockedCarPlate, updated by user : $userId" }
        return updateBlockInfoInCarRepo(blockingCarPlate, blockedCarPlate, userId, userStatus, true)
            .onFailure { logger.warn(it) { "Failed to bock Car : $blockedCarPlate by Car : $blockingCarPlate, updated by user : $userId in car repo" } }
            .map { updateInfo ->
                updateBlockInfoInUsersCarsRepo(updateInfo, true)
                    .onFailure { logger.warn(it) { "Failed to bock Car : $blockedCarPlate by Car : $blockingCarPlate, updated by user : $userId in usersCars repo" } }
                    .map { sendBlockedNotification(updateInfo.blockedCar) }
            }
    }

    fun releaseCar(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long,
        userStatus: UserStatus,
    ): Result<Unit, RHException> {
        logger.info { "Try to release that Car : $blockingCarPlate from Car : $blockedCarPlate, updated by user : $userId" }
        return updateBlockInfoInCarRepo(blockingCarPlate, blockedCarPlate, userId, userStatus, false)
            .onFailure { logger.warn(it) { "Failed to release Car : $blockingCarPlate from Car : $blockedCarPlate, updated by user : $userId" } }
            .map { updateInfo ->
                updateBlockInfoInUsersCarsRepo(updateInfo, false)
                    .onFailure { logger.warn(it) { "Failed to release Car : $blockedCarPlate from Car : $blockingCarPlate, updated by user : $userId in usersCars repo" } }
            }
    }

    fun sendFreeMe(blockedCarPlate: String): Result<Unit, RHException> {
        return carsRepository.findByPlateNumberSafe(blockedCarPlate)
            .onFailure { logger.warn(it) { "Failed" } }
            .map { sendNeedToGoNotification(it) }
    }

    private fun sendNeedToGoNotification(blockedCar: Car) {
        logger.info { "Try to Send NEED_TO_GO Notification from ${blockedCar.plateNumber}, isBlocked = ${blockedCar.isBlocked}" }
        if (!blockedCar.isBlocked) return
        usersCarsRepository.findByBlockedCar(blockedCar)?.forEach { currentCar ->
            sendNotification(currentCar.user, NotificationsKind.NEED_TO_GO, currentCar.car)
            logger.info { "Continue to Notify Car ${currentCar.blockingCar?.plateNumber}" }
            sendNeedToGoNotification(currentCar.car)
        }
    }

    private fun sendBlockedNotification(blockedCar: Car) {
        usersCarsRepository.findByCarSafe(blockedCar)
            .onFailure { logger.warn(it) { "Failed" } }
            .onSuccess {
                it.forEach { usersCars ->
                    logger.info { "Try to Send BEEN_BLOCKED Notification to User ${usersCars.user.userId} for Car ${blockedCar.plateNumber}" }
                    sendNotification(usersCars.user, NotificationsKind.BEEN_BLOCKED)
                }
            }
    }

    private fun updateBlockInfoInCarRepo(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long,
        userStatus: UserStatus,
        block: Boolean,
    ): Result<CarObject, RHException> {
        logger.info { "Update Block Info in Car Repo, Car $blockingCarPlate ${blockToString(block)} Car $blockedCarPlate" }
        return carService.findAndUpdateCar(blockingCarPlate, userId, userStatus, block, CarStatus.BLOCKING)
            .onFailure { logger.warn(it) { "Failed to find and update blockingCar : $blockingCarPlate" } }
            .andThen { blockingCar ->
                carService.findAndUpdateCar(blockedCarPlate, userId, userStatus, block, CarStatus.BLOCKED)
                    .onFailure { logger.warn(it) { "Failed to find and update blockedCar : $blockedCarPlate" } }
                    .map { blockedCar -> CarObject(blockingCar, blockedCar) }
            }
    }

    private fun updateBlockInfoInUsersCarsRepo(
        carObject: CarObject,
        block: Boolean,
    ): Result<Unit, RHException> {
        return with(carObject) {
            logger.info { "Update Block Info in UsersCars Repo, Car $blockingCar ${blockToString(block)} Car $blockedCar" }

            usersCarsRepository.findByCarSafe(blockingCar)
                .onFailure { logger.warn(it) { "Failed to find blockingCar : ${carObject.blockingCar.plateNumber} in updateBlockInfoInUsersCarsRepo" } }
                .onSuccess { currentBlockingUserCarList ->
                    currentBlockingUserCarList.forEach {
                        if (block) it.blocking(blockedCar) else it.unblocking()
                        usersCarsRepository.saveSafe(it)
                            .onFailure { ex -> logger.warn(ex) { "Failed to update blocking car in updateBlockInfoInUsersCarsRepo" } }
                    }
                }
                .andThen {
                    usersCarsRepository.findByCarSafe(blockedCar)
                        .onFailure { logger.warn(it) { "Failed to find blockedCar : ${carObject.blockedCar.plateNumber} in updateBlockInfoInUsersCarsRepo" } }
                        .onSuccess { currentBlockedUserCarList ->
                            currentBlockedUserCarList.forEach {
                                if (block) it.blockedBy(blockingCar) else it.unblocked()
                                usersCarsRepository.saveSafe(it)
                                    .onFailure { ex -> logger.warn(ex) { "Failed to update blocked car in updateBlockInfoInUsersCarsRepo" } }
                            }
                        }
                }
        }.map {}
    }

    private fun blockToString(block: Boolean) = if (block) "block" else "release"

    private fun sendNotification(user: User, notificationsKind: NotificationsKind, car: Car? = null) {
        logger.info { "sending $notificationsKind to user : ${user.userId} with car : ${car?.plateNumber}" }
        when (notificationsKind) {
            NotificationsKind.NEED_TO_GO -> notificationService.sendPushNotification(
                user.pushNotificationToken,
                requireNotNull(car){"Car can't be null when sending NEED_TO_GO notification"}.plateNumber
            )
            NotificationsKind.BEEN_BLOCKED -> notificationService.sendPushNotification(
                user.pushNotificationToken, null
            )
        }
    }
}

data class CarObject(
    var blockingCar: Car,
    var blockedCar: Car,
)







