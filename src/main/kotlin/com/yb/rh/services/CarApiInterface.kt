package com.yb.rh.services

import com.yb.rh.common.Countries
import com.yb.rh.entities.CarDTO
import com.yb.rh.services.countryCarJson.CountryCarJson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service

@Service
class CarApiInterface(private val countryCarJson: CountryCarJson) {

    fun getCarInfo(plateNumber: String, country: Countries): CarDTO {
        val url = getCarByCountry(plateNumber, country)

        val request = Request.Builder().url(url).build()

        val body = OkHttpClient().newCall(request).execute().body!!

        val actualCountryCarJsonHandler = countryCarJson.getCountryCarJsonHandler(country)

        return actualCountryCarJsonHandler.getCarDTO(body)
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

