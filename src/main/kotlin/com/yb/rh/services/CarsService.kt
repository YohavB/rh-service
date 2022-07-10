package com.yb.rh.services

import com.yb.rh.entities.Cars
import com.yb.rh.entities.CarsDTO
import com.yb.rh.entities.UsersCars
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Service
class CarsService(
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Cars> = carsRepository.findAll().toList()

    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): CarsDTO? {
        logger.info { "Try to find Car : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.toDto()
    }

    fun createOrUpdateCar(
        carsDTO: CarsDTO,
        userId: Long?
    ): CarsDTO {
        logger.info { "Try to create or update Car : ${carsDTO.plateNumber} of user : $userId " }

        val currentCar = Cars.fromDto(carsDTO)

        carsRepository.save(currentCar)
        userId?.let { userId ->
            usersRepository.findByUserId(userId)?.let { currentUser ->
                usersCarsRepository.save(UsersCars(currentUser, currentCar))
            }
        }
        return carsDTO
    }
}