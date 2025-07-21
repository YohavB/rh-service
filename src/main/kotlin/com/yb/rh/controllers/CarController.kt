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
 */
@RestController
@RequestMapping("/api/v1/car")
class CarController(
    private val carService: CarService
) : BaseController() {

    /**
     * Finds a car by its plate number and country and adds it to the database if not already present.
     * @body requestDTO The request containing the plate number and country of the car
     * @return The car details if found
     */
    @PostMapping
    fun findCarRequest(
        @RequestBody requestDTO: FindCarRequestDTO
    ): CarDTO = carService.getCarOrCreateRequest(requestDTO)
}