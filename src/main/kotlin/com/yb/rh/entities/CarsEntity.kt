package com.yb.rh.entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "cars")
data class Cars(
    @Id
    @Column(unique = true)
    var plateNumber: String,
    @Enumerated(EnumType.STRING)
    var brand: Brands,
    var model: String,
    @Enumerated(EnumType.STRING)
    var color: Colors,
    var carLicenseExpireDate: LocalDateTime?,
    var isBlocking: Boolean,
    var isBlocked: Boolean,

    ) {

    fun beingBlocking() {
        this.isBlocking = true
    }

    fun beingBlocked() {
        this.isBlocked = true
    }

    fun unblocking() {
        this.isBlocking = false
    }

    fun unblocked() {
        this.isBlocked = false
    }


    fun toDto() = CarsDTO(plateNumber, brand, model, color, carLicenseExpireDate, isBlocking, isBlocked)

    companion object {
        fun fromDto(carsDTO: CarsDTO) = Cars(
            carsDTO.plateNumber,
            carsDTO.brand,
            carsDTO.model,
            carsDTO.color,
            carsDTO.carLicenseExpireDate,
            carsDTO.isBlocking,
            carsDTO.isBlocked
        )
    }
}

data class CarsDTO(
    val plateNumber: String,
    val brand: Brands,
    val model: String,
    val color: Colors,
    var carLicenseExpireDate: LocalDateTime?,
    var isBlocking: Boolean = false,
    var isBlocked: Boolean = false
) {
    companion object {
        fun returnTest(): CarsDTO {
            return CarsDTO("Test", Brands.UNKNOWN, "Test", Colors.UNKNOWN, LocalDateTime.now())
        }
    }
}