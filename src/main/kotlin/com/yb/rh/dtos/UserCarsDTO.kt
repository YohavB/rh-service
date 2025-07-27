package com.yb.rh.dtos

import jakarta.validation.constraints.NotNull

data class UserCarDTO(
    @NotNull
    val user: UserDTO,
    @NotNull
    val car: CarDTO
)

data class UserCarRequestDTO(
    @NotNull
    val userId: Long,
    @NotNull
    val carId: Long,
)

data class UserCarsDTO(
    @NotNull
    val user: UserDTO,
    val cars: List<CarDTO>
)

data class CarUsersDTO(
    @NotNull
    val car: CarDTO,
    val users: List<UserDTO>
)

data class UserCarsThinDTO(
    @NotNull
    val userId: Long,
    val carsIds: List<Long>
)

data class CarUsersThinDTO(
    @NotNull
    val carId: Long,
    val usersIds: List<Long>
)

