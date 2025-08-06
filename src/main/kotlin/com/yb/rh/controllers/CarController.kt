package com.yb.rh.controllers

import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.FindCarRequestDTO
import com.yb.rh.services.CarService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller handling car-related operations in the Rush Hour service
 * Provides car lookup, creation, and information retrieval functionality
 */
@RestController
@RequestMapping("/api/v1/car")
class CarController(
    private val carService: CarService
) {
    /**
     * Find or create a car by plate number and country
     * Searches for existing car in database, if not found, fetches car information from external API
     * and creates a new car record. Returns car details with ownership status.
     * 
     * @param requestDTO Request containing plate number, country, and optional user ID
     * @return Car details including ownership status (hasOwner field)
     * @throws RHException if plate number is invalid or external API fails
     */
    @PostMapping
    fun getCarOrCreateRequest(
        @RequestBody requestDTO: FindCarRequestDTO
    ): CarDTO = carService.getCarOrCreateRequest(requestDTO)
}