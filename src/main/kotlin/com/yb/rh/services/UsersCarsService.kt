package com.yb.rh.services

import com.yb.rh.common.NotificationsKind
import com.yb.rh.entities.*
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import mu.KotlinLogging
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service


@Service
class UsersCarsService(
    private val repository: UsersCarsRepository,
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val carsService: CarsService,
//    private val usersService: UsersService,
) {
    private val logger = KotlinLogging.logger {}

    fun getAllUsersCars(): MutableIterable<UsersCars> =
        repository.findAll()


    fun getUsersCarsByPlateNumber(plateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find UsersCars by plate : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.let {
            repository.findByCar(it)
        }?.map { it.toDto() } ?: emptyList()
    }

    fun getCarsByUserId(userId: Long): List<UsersCarsDTO> {
        logger.info { "Try to find UsersCars by userId : $userId" }
        return usersRepository.findByUserId(userId).let { repository.findByUser(it) }?.map { it.toDto() }
            ?: emptyList()
    }

    fun getBlockingCarByBlockedPlateNumber(blockedPlateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find Blocking Car by Blocked Car : $blockedPlateNumber" }
        return carsService.findByPlateNumber(blockedPlateNumber)
            .let { repository.findByBlockedCar(it.toEntity()) }?.map { it.toDto() } ?: emptyList()
    }

    fun getBlockedCarByBlockingPlateNumber(blockingPlateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find Blocked Car by Blocking Car : $blockingPlateNumber" }
        return carsService.findByPlateNumber(blockingPlateNumber)
            .let { repository.findByBlockingCar(it.toEntity()) }?.map { it.toDto() } ?: emptyList()
    }

    fun updateBlockedCar(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long
    ) {
        logger.info {
            "Try to update that Car : $blockingCarPlate is blocking Car : $blockedCarPlate," +
                    " updated by user : $userId"
        }

        val updateInfo = updateBlockInfo(blockingCarPlate, blockedCarPlate, userId, true)
        updateReleaseInfo(updateInfo, true)

        sendBlockedNotification(updateInfo.blockedCar)
    }

    fun releaseCar(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long
    ) {
        logger.info {
            "Try to release that Car : $blockingCarPlate from Car : $blockedCarPlate," +
                    " updated by user : $userId"
        }

        val updateInfo = updateBlockInfo(blockingCarPlate, blockedCarPlate, userId, false)
        updateReleaseInfo(updateInfo, false)

        sendNeedToGoNotification(updateInfo.blockedCar)
    }

    fun sendBlockedNotification(blockedCar: Car) {
        repository.findByCar(blockedCar)?.forEach { sendNotification(it.user, NotificationsKind.BEEN_BLOCKED) }
    }

    fun sendNeedToGoNotification(blockedCar: Car) {
        if (!blockedCar.isBlocked) return
        repository.findByBlockedCar(blockedCar)?.forEach {
            sendNotification(it.user, NotificationsKind.NEED_TO_GO)
            it.blockingCar?.let { blockingCar -> sendNeedToGoNotification(blockingCar) }
        }
    }

    fun getByUserAndPlate(userId: Long, plateNumber: String): UsersCars? {
        val user = usersRepository.findByUserId(userId)
        val car = carsRepository.findByPlateNumber(plateNumber)!!

        return repository.findByUserAndCar(user, car)
    }

    private fun updateBlockInfo(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userId: Long,
        block: Boolean
    ): UserCarObject {
        val blockingCar = carsService.findByPlateNumber(blockingCarPlate).toEntity()
        if (block) blockingCar.beingBlocking() else blockingCar.unblocking()
        carsRepository.save(blockingCar)

        val blockedCar = carsService.findByPlateNumber(blockedCarPlate).toEntity()
        if (block) blockedCar.beingBlocked() else blockedCar.unblocked()
        carsRepository.save(blockedCar)

        val user = usersRepository.findByUserId(userId)

        return UserCarObject(blockingCar, blockedCar, user)
    }

    private fun updateReleaseInfo(
        userCarObject: UserCarObject,
        block: Boolean
    ) {
        val currentBlockingUserCar = repository.findByUserAndCar(userCarObject.user, userCarObject.blockingCar)
        if (block) currentBlockingUserCar.blocking(userCarObject.blockedCar) else currentBlockingUserCar.unblocking()
        repository.save(currentBlockingUserCar)

        val currentBlockedUserCar = repository.findByCar(userCarObject.blockedCar)
        currentBlockedUserCar?.forEach {
            if (block) it.blockedBy(userCarObject.blockingCar) else it.unblocked()
            repository.save(it)
        } ?: throw NotFoundException()
    }

    private fun sendNotification(user: User, notificationsKind: NotificationsKind) {
        logger.info { "sending $notificationsKind to user : ${user.userId}, ${user.firstName} ${user.lastName}" }
        //todo implements this with google
    }
}

data class UserCarObject(
    var blockingCar: Car,
    var blockedCar: Car,
    var user: User
)







