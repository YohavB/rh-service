package com.yb.rh.services

import com.google.gson.GsonBuilder
import com.yb.rh.common.Countries
import com.yb.rh.entities.Cars
import com.yb.rh.entities.CarsDTO
import com.yb.rh.entities.UsersCars
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import com.yb.rh.services.ilcarapi.IlCarJson
import mu.KotlinLogging
import okhttp3.*
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam

@Service
class CarsService(
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository,
    private val carApiInterface: CarApiInterface
) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Cars> = carsRepository.findAll().toList()

    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): CarsDTO? {
        logger.info { "Try to find Car : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.toDto()
            ?: getCarInfo(plateNumber).let { carsRepository.save(Cars.fromDto(it)).toDto() }
    }


    fun createOrUpdateCar(
        plateNumber: String,
        userId: Long?
    ) {
        logger.info { "Try to create or update Car : $plateNumber of user : $userId " }

        val carDTO = getCarInfo(plateNumber)

        carsRepository.findByPlateNumber(plateNumber)?.let { currentCar ->
            userId?.let { it ->
                usersRepository.findByUserId(it)?.let { currentUser ->
                    usersCarsRepository.save(UsersCars(currentUser, currentCar))
                }
            }

        }
    }

    private fun getCarInfo(plateNumber: String): CarsDTO {
        logger.info { "Try to get Car Info $plateNumber" }

        //todo get country
        val url = carApiInterface.getCarInfo(plateNumber, Countries.IL)

        val request = Request.Builder().url(url).build()

        val body = OkHttpClient().newCall(request).execute().body

        val carInfo = GsonBuilder().create().fromJson(body?.string(), IlCarJson::class.java)

        return carInfo.toCarDto()
    }
}
