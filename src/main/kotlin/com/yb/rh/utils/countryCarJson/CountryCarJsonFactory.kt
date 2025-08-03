package com.yb.rh.utils.countryCarJson

import com.yb.rh.enum.Countries
import org.springframework.stereotype.Service

@Service
class CountryCarJsonFactory() {
    fun getCountryCarJsonHandler(country: Countries): ICarJsonHandler =
        when (country) {
            Countries.IL -> IlCarJsonHandler()
            else -> throw IllegalArgumentException("Car Api is not implemented for $country")
        }
}