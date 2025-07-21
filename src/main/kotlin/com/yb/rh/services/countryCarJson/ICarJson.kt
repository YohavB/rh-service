package com.yb.rh.services.countryCarJson

import com.yb.rh.common.Countries
import com.yb.rh.dtos.CarDTO
import okhttp3.ResponseBody

interface ICarJsonHandler {
    val country: Countries
    fun getCarDTO(rawResponse: ResponseBody): CarDTO
}