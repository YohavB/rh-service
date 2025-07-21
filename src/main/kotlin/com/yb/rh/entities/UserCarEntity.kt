package com.yb.rh.entities

import com.yb.rh.dtos.UserCarDTO
import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "users_cars")
data class UserCar(
    @ManyToOne
    @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    var user: User,

    @ManyToOne
    @NotNull
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    var car: Car,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
) {
    fun toDto() = UserCarDTO(
        user = user.toDto(),
        car = car.toDto()
    )
}