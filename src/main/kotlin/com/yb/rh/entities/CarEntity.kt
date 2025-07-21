package com.yb.rh.entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.dtos.CarDTO
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "cars")
data class Car(
    @NotNull
    @Column(unique = true, name = "plate_number")
    val plateNumber: String,

    @NotNull
    @Enumerated(EnumType.STRING)
    val country: Countries,

    @Enumerated(EnumType.STRING)
    val brand: Brands = Brands.UNKNOWN,

    val model: String,

    @Enumerated(EnumType.STRING)
    val color: Colors = Colors.UNKNOWN,

    var carLicenseExpireDate: LocalDateTime? = null,

    @CreationTimestamp
    @Column(name = "creation_time")
    val creationTime: LocalDateTime? = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) {
    fun toDto() = CarDTO(id, plateNumber, country, brand, model, color, carLicenseExpireDate)

    companion object {
        fun fromDto(carDTO: CarDTO) = Car(
            carDTO.plateNumber,
            carDTO.country,
            carDTO.brand,
            carDTO.model,
            carDTO.color,
            carDTO.carLicenseExpireDate
        )
    }
}