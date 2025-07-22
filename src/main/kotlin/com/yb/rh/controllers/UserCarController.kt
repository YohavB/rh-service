package com.yb.rh.controllers

import com.yb.rh.dtos.UserCarRequestDTO
import com.yb.rh.dtos.UserCarsDTO
import com.yb.rh.services.MainService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Controller handling relationships between users and cars
 */
@RestController
@RequestMapping("/api/v1/user-car")
class UserCarController(
    private val mainService: MainService,
) : BaseController() {

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