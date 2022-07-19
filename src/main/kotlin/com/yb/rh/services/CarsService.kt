package com.yb.rh.services

import com.google.gson.GsonBuilder
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsDTO
import com.yb.rh.entities.UsersCars
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import com.yb.rh.services.countryCarJson.CountryCarJson
import mu.KotlinLogging
import okhttp3.*
import org.springframework.stereotype.Service

@Service
class CarsService(
    private val carsRepository: CarsRepository,
    private val usersRepository: UsersRepository,
    private val usersCarsRepository: UsersCarsRepository,
    private val carApiInterface: CarApiInterface,
    private val countryCarJson: CountryCarJson
) {

    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Car> = carsRepository.findAll().toList()

    fun findByPlateNumber(plateNumber: String): CarsDTO {
        logger.info { "Try to find Car : $plateNumber" }
        return carsRepository.findByPlateNumber(plateNumber)?.toDto()
            ?: getCarInfo(plateNumber, Countries.IL).let { carsRepository.save(Car.fromDto(it)).toDto() }
    }


    fun createOrUpdateCar(
        plateNumber: String,
        userId: Long?
    ) {
        logger.info { "Try to create or update Car : $plateNumber of user : $userId " }

        val currentCar = getCarInfo(plateNumber, Countries.IL).toEntity()

        carsRepository.save(currentCar)

        userId?.let { it ->
            usersRepository.findByUserId(it)?.let { currentUser ->
                val userCar = UsersCars(currentUser, currentCar)
                println(usersCarsRepository.save(userCar))
            }
        }
    }

    private fun getCarInfo(plateNumber: String, country: Countries): CarsDTO {
        logger.info { "Try to get Car Info $plateNumber from $country" }

        //todo get country
        val url = carApiInterface.getCarInfo(plateNumber, country)

        val request = Request.Builder().url(url).build()

        val body = OkHttpClient().newCall(request).execute().body

        val actualCountryCarJson = countryCarJson.getCountryCarJson(country)

        val carInfo = GsonBuilder().create().fromJson(body?.string(), actualCountryCarJson)

        return carInfo.toCarDto()
    }

    fun updateBlockedAndBlockingCar(blockingCarPlate: String,
                                    blockedCarPlate: String){

    }
}
