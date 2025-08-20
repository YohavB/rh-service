package com.yb.rh.controllers

import com.yb.rh.dtos.UserCarRequestDTO
import com.yb.rh.dtos.UserCarsDTO
import com.yb.rh.services.MainService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between users and cars
 * Provides user-car association management including assignment and removal
 */
@RestController
@RequestMapping("/api/v1/user-car")
class UserCarController(
    private val mainService: MainService,
) {
    /**
     * Assign a car to the current user
     * Creates a user-car relationship if it doesn't already exist
     * 
     * @param userCar Request containing user ID and car ID for assignment
     * @return Updated list of cars associated with the user
     * @throws RHException if user or car is not found
     */
    @PostMapping
    fun createUserCar(
        @Valid @RequestBody userCar: UserCarRequestDTO,
    ): UserCarsDTO = mainService.createUserCar(userCar)

    /**
     * Get all cars associated with the current user
     * @return List of cars owned by the current user
     * @throws RHException if user is not found or token is invalid
     */
    @GetMapping
    fun getUserCarsByUserId(): UserCarsDTO = mainService.getUserCarsByUser()

    /**
     * Remove a car from the current user
     * Deletes the user-car relationship
     * 
     * @param userCar Request containing user ID and car ID for removal
     * @return Updated list of cars associated with the user
     * @throws RHException if user-car relationship is not found
     */
    @DeleteMapping
    fun deleteUserCar(
        @Valid @RequestBody userCar: UserCarRequestDTO,
    ): UserCarsDTO = mainService.deleteUserCar(userCar)
}