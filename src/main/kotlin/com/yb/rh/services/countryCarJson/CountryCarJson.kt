package com.yb.rh.services.countryCarJson

import com.yb.rh.common.Countries
import org.springframework.stereotype.Service

@Service
class CountryCarJson() {
    fun getCountryCarJsonHandler(country: Countries): ICarJsonHandler =
        when (country) {
            Countries.IL -> IlCarJsonHandler()
            else -> throw IllegalArgumentException("Car Api is not implemented for $country")
        }
}