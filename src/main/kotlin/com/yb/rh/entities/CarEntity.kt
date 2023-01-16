package com.yb.rh.entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "cars")
data class Car(
    @Id
    @NotNull
    @Column(unique = true, name = "plate_number")
    var plateNumber: String,
    @Enumerated(EnumType.STRING)
    var brand: Brands,
    var model: String,
    @Enumerated(EnumType.STRING)
    var color: Colors,
    var carLicenseExpireDate: LocalDateTime? = null,
    var isBlocking: Boolean = false,
    var isBlocked: Boolean = false,
    @CreationTimestamp
    @Column(name = "creation_time")
    var creationTime: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: LocalDateTime? = null,
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


    fun toDto() = CarDTO(plateNumber, brand, model, color, carLicenseExpireDate, isBlocking, isBlocked)

    companion object {
        fun fromDto(carDTO: CarDTO) = Car(
            carDTO.plateNumber,
            carDTO.brand,
            carDTO.model,
            carDTO.color,
            carDTO.carLicenseExpireDate,
            carDTO.isBlocking,
            carDTO.isBlocked
        )
    }
}

data class CarDTO(
    val plateNumber: String,
    val brand: Brands,
    val model: String,
    val color: Colors,
    var carLicenseExpireDate: LocalDateTime?,
    var isBlocking: Boolean = false,
    var isBlocked: Boolean = false,
) {
    companion object {
        fun returnTest(): CarDTO {
            return CarDTO("Test", Brands.UNKNOWN, "Test", Colors.UNKNOWN, LocalDateTime.now())
        }
    }

    fun toEntity() = Car.fromDto(this)
}