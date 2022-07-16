package com.yb.rh.services

import com.yb.rh.entities.*
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service


@Service
class UsersCarsService(
    private val repository: UsersCarsRepository,
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository
) {
    private val logger = KotlinLogging.logger {}

    fun getAllUsersCars() =
        repository.findAll().map { it?.toDto() ?: emptyList<UsersCarsDTO>() }


    fun getUsersCarsByPlateNumber(plateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find UsersCars by plate : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.let {
            repository.findByUserCar(it)
        }?.map { it.toDto() } ?: emptyList()
    }

    fun getCarsByUserId(userId: Long): List<UsersCarsDTO> {
        logger.info { "Try to find UsersCars by userId : $userId" }
        return usersRepository.findByUserId(userId)?.let { repository.findByUserId(it) }?.map { it.toDto() }
            ?: emptyList()
    }

    fun getBlockingCarByBlockedPlateNumber(blockedPlateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find Blocking Car by Blocked Car : $blockedPlateNumber" }
        return carsRepository.findByPlateNumber(blockedPlateNumber)
            ?.let { repository.findBlockingCarsByBlockedCar(it) }?.map { it.toDto() } ?: emptyList()
    }

    fun getBlockedCarByBlockingPlateNumber(blockingPlateNumber: String): List<UsersCarsDTO> {
        logger.info { "Try to find Blocked Car by Blocking Car : $blockingPlateNumber" }
        return carsRepository.findByPlateNumber(blockingPlateNumber)
            ?.let { repository.findBlockedCarsByBlockingCar(it) }?.map { it.toDto() } ?: emptyList()
    }

    fun updateBlockedCar(
        blockingCarPlate: String,
        blockedCarPlate: String,
        userDto: UsersDTO
    ) {
        logger.info {
            "Try to update that Car : $blockingCarPlate is blocking Car : $blockedCarPlate," +
                    " updated by user : ${userDto.userId} (${userDto.firstName} ${userDto.lastName})"
        }

        val blockedCar = carsRepository.findByPlateNumber(blockedCarPlate)!!
        val blockingCar = carsRepository.findByPlateNumber(blockingCarPlate)!!
        val user = Users.fromDto(userDto)

        blockedCar.isBlocked
        blockingCar.isBlocking

        carsRepository.save(blockedCar)
        carsRepository.save(blockingCar)

        val currentBlockingUserCar = repository.findByUserIdAndUserCar(user, blockingCar)
        val currentBlockedUserCar = repository.findByUserIdAndUserCar(user, blockedCar)

        currentBlockingUserCar?.blocking(blockedCar)
        currentBlockedUserCar?.blockedBy(blockingCar)

        repository.save(currentBlockingUserCar!!)
        repository.save(currentBlockedUserCar!!)
    }

    fun releaseCar(
        blockingCarDTO: CarsDTO,
        blockedCarDTO: CarsDTO,
        userDto: UsersDTO
    ) {
        logger.info {
            "Try to release that Car : ${blockingCarDTO.plateNumber} from  Car : ${blockedCarDTO.plateNumber}," +
                    " updated by user : ${userDto.userId} (${userDto.firstName} ${userDto.lastName})"
        }

        val blockedCar = Cars.fromDto(blockedCarDTO)
        val blockingCar = Cars.fromDto(blockedCarDTO)
        val user = Users.fromDto(userDto)

        blockedCar.unblocked()
        blockingCar.unblocking()

        carsRepository.save(blockedCar)
        carsRepository.save(blockingCar)

        val currentBlockingUserCar = repository.findByUserIdAndUserCar(user, blockingCar)
        val currentBlockedUserCar = repository.findByUserIdAndUserCar(user, blockedCar)

        currentBlockingUserCar?.unblocking()
        currentBlockedUserCar?.unblocked()

        repository.save(currentBlockingUserCar!!)
        repository.save(currentBlockedUserCar!!)
    }

}