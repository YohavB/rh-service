package com.yb.rh.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.entities.Car
import java.time.LocalDateTime

data class CarDTO(
    val id: Long,
    val plateNumber: String,
    val country: Countries,
    val brand: Brands,
    val model: String,
    val color: Colors,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    var carLicenseExpireDate: LocalDateTime?
) {
    fun toEntity() = Car.fromDto(this)
}

data class FindCarRequestDTO(
    val plateNumber: String,
    val country: Countries,
    val userId: Long? = null
)