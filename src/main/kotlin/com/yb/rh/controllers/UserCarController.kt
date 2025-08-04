package com.yb.rh.controllers

import com.yb.rh.dtos.UserCarRequestDTO
import com.yb.rh.dtos.UserCarsDTO
import com.yb.rh.services.MainService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between users and cars
 */
@RestController
@RequestMapping("/api/v1/user-car")
class UserCarController(
    private val mainService: MainService,
) {
    @PostMapping
    fun createUserCar(
        @Valid @RequestBody userCar: UserCarRequestDTO,
    ): UserCarsDTO = mainService.createUserCar(userCar)

    @GetMapping("/by-user-id")
    fun getUserCarsByUserId(
        @RequestParam(name = "userId") userId: Long,
    ): UserCarsDTO = mainService.getUserCarsByUser(userId)

    @DeleteMapping
    fun deleteUserCar(
        @Valid @RequestBody userCar: UserCarRequestDTO,
    ): UserCarsDTO = mainService.deleteUserCar(userCar)
}