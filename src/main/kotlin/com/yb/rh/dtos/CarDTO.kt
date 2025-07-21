package com.yb.rh.dtos

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import java.time.LocalDateTime

data class CarDTO(
    val id: Long,
    val plateNumber: String,
    val country: Countries,
    val brand: Brands,
    val model: String,
    val color: Colors,
    var carLicenseExpireDate: LocalDateTime?
) {
    fun toEntity() = Car.fromDto(this)
}

data class FindCarRequestDTO(
    val plateNumber: String,
    val country: Countries,
    val userId: Long? = null
)