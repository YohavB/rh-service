package com.yb.rh.dtos

data class UserCarDTO(
    val user: UserDTO,
    val car: CarDTO
)

data class UserCarRequestDTO(
    val userId: Long,
    val carId: Long,
)

data class UserCarsDTO(
    val user: UserDTO,
    val cars: List<CarDTO>
)

data class CarUsersDTO(
    val car: CarDTO,
    val users: List<UserDTO>
)

data class UserCarsThinDTO(
    val userId: Long,
    val carsIds: List<Long>
)

data class CarUsersThinDTO(
    val carId: Long,
    val usersIds: List<Long>
)

