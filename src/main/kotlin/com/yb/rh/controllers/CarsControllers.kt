package com.yb.rh.controllers

import com.yb.rh.common.Countries
import com.yb.rh.services.CarService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller handling car-related operations in the Rush Hour service
 */
@RestController
@RequestMapping("/api/cars")
class CarsController(
    private val carService: CarService
) : BaseController() {  // Inherit from BaseController

    /**
     * Retrieves all cars registered in the system
     * @return List of all cars
     */
    @GetMapping("/")
    fun findAll() = carService.findAll()

    /**
     * Finds a car by its plate number and country
     * @param plateNumber The license plate number of the car
     * @param country The country where the car is registered
     * @return The car details if found
     */
    @GetMapping("/by-plate")
    fun findByPlateNumber(
        @RequestParam(name = "plateNumber") plateNumber: String,
        @RequestParam(name = "country", required = true) country: Countries,
    ): ResponseEntity<out RHResponse> {
        val result = carService.findByPlateNumber(plateNumber, country)
        return handleServiceResult(result, "Successfully found car by plate", "Failed to find car by plate")
    }

    /**
     * Creates a new car or updates an existing one
     * @param plateNumber The license plate number of the car
     * @param country The country where the car is registered
     * @param userId Optional ID of the user associated with the car
     * @return The created or updated car details
     */
    @PostMapping("/car")
    fun createOrUpdateCar(
        @RequestParam(name = "plateNumber", required = true) plateNumber: String,
        @RequestParam(name = "country", required = true) country: Countries,
        @RequestParam(name = "userId", required = false) userId: Long?
    ): ResponseEntity<out RHResponse> {
        val result = carService.createOrUpdateCar(plateNumber, country, userId)
        return handleServiceResult(result, "Successfully created or updated car", "Failed to create or update car")
    }
}