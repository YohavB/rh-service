package com.yb.rh.controllers

import com.yb.rh.dtos.CarRelationsDTO
import com.yb.rh.dtos.CarsRelationRequestDTO
import com.yb.rh.services.MainService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between cars
 * Provides car blocking/unblocking functionality and relationship management
 */
@RestController
@RequestMapping("/api/v1/car-relations")
class CarRelationsController(
    private val mainService: MainService,
) {
    /**
     * Create a blocking relationship between two cars
     * Establishes that one car is blocking another car from leaving
     * Sends notifications to car owners if they are registered in the system
     * 
     * @param carsRelationRequestDTO Request containing blocking car ID, blocked car ID, and situation
     * @return Car relations with blocking/blocked information and notification status message
     * @throws RHException if cars are not found or relationship already exists
     */
    @PostMapping
    fun createCarsRelations(
        @Valid @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): List<CarRelationsDTO> = mainService.createCarsRelations(carsRelationRequestDTO)

    /**
     * Get blocking relationships for a specific car
     * @param carId ID of the car to get relationships for
     * @return Car relations showing which cars are blocking/blocked by the specified car
     * @throws RHException if car is not found
     */
    @GetMapping("/by-car-id")
    fun getCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    ): CarRelationsDTO = mainService.getCarRelationsByCarId(carId)

    /**
     * Get all car relationships for the current user's cars
     * Returns blocking relationships for all cars owned by the current user
     * @return List of car relations for all user's cars
     * @throws RHException if user is not found or token is invalid
     */
    @GetMapping("/by-user")
    fun getUserCarRelations(): List<CarRelationsDTO> = mainService.getUserCarRelations()

    /**
     * Remove a blocking relationship between two cars
     * Removes the blocking relationship and sends "free to go" notifications
     * 
     * @param carsRelationRequestDTO Request containing blocking car ID, blocked car ID, and situation
     * @return Car relations with updated blocking/blocked information and notification status message
     * @throws RHException if relationship is not found
     */
    @DeleteMapping
    fun deleteCarRelations(
        @Valid @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): CarRelationsDTO = mainService.deleteCarsRelations(carsRelationRequestDTO)

    /**
     * Remove all blocking relationships for a specific car
     * Deletes all relationships where the car is either blocking or being blocked
     * 
     * @param carId ID of the car to remove all relationships for
     * @return 200 OK response
     * @throws RHException if car is not found
     */
    @DeleteMapping("/all-by-car-id")
    fun deleteAllCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    )= mainService.deleteAllCarRelationsByCarId(carId)
}