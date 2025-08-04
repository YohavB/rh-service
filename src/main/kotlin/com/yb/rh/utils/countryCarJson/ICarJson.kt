package com.yb.rh.utils.countryCarJson

import com.yb.rh.dtos.CarDTO
import com.yb.rh.enum.Countries
import okhttp3.ResponseBody

interface ICarJsonHandler {
    val country: Countries
    fun getCarDTO(rawResponse: ResponseBody): CarDTO
}