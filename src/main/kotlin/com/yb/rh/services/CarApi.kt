package com.yb.rh.services

import com.yb.rh.common.Countries
import com.yb.rh.dtos.CarDTO
import com.yb.rh.services.countryCarJson.CountryCarJsonFactory
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class CarApi(private val countryCarJsonFactory: CountryCarJsonFactory) {

    private val logger = KotlinLogging.logger {}


    fun getCarInfo(plateNumber: String, country: Countries): CarDTO {
        logger.info { "Try to get Car Info $plateNumber from $country" }

        return try {
        val url = getCarByCountry(plateNumber, country)

        val request = Request.Builder().url(url).build()

        val body = OkHttpClient().newCall(request).execute().body!!

            val countryCarJsonHandler = countryCarJsonFactory.getCountryCarJsonHandler(country)

            countryCarJsonHandler.getCarDTO(body)
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Failed to fetch car info for plate number: $plateNumber in country: $country",
                e
            )
        }
    }

    private fun getCarByCountry(plateNumber: String, country: Countries): String {
        return when (country) {
            Countries.IL -> getIsraelCar(plateNumber)
            else -> throw IllegalArgumentException("Car Api is not implemented for $country")
        }
    }

    // Israeli Car Api
    private fun getIsraelCar(plateNumber: String) =
        "https://data.gov.il/api/3/action/datastore_search?resource_id=053cea08-09bc-40ec-8f7a-156f0677aff3&q=$plateNumber"

}

