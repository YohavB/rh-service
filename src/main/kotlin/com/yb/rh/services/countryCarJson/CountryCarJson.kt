package com.yb.rh.services.countryCarJson

import com.yb.rh.common.Countries
import org.springframework.stereotype.Service

@Service
class CountryCarJson() {

    //Todo return different class
    fun getCountryCarJson(country: Countries) =
        when (country) {
            Countries.IL -> IlCarJson::class.java
        }

}