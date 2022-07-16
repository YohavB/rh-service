package com.yb.rh.services

import com.yb.rh.common.Countries
import com.yb.rh.services.ilcarapi.IlCarJson
import org.springframework.stereotype.Service
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import java.util.Locale.IsoCountryCode

@Service
interface CarApiInterface {

    fun getCarInfo(plateNumber: String, country: Countries): String {
        return when (country) {
            Countries.IL -> getIsraelCar(plateNumber)
        }
    }

    // Israeli Car Api
    private fun getIsraelCar(plateNumber: String) =
        "https://data.gov.il/api/3/action/datastore_search?resource_id=053cea08-09bc-40ec-8f7a-156f0677aff3&q=$plateNumber"

}

