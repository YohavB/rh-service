package com.yb.rh.controllers

import com.yb.rh.common.Countries
import com.yb.rh.services.CarService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cars")
class CarsController(
    private val carService: CarService
) : BaseController() {  // Inherit from BaseController

    @GetMapping("/")
    fun findAll() = carService.findAll()

    @GetMapping("/by-plate")
    fun findByPlateNumber(
        @RequestParam(name = "plateNumber") plateNumber: String,
        @RequestParam(name = "country", required = true) country: Countries,
    ): ResponseEntity<out RHResponse> {
        val result = carService.findByPlateNumber(plateNumber, country)
        return handleServiceResult(result, "Successfully found car by plate", "Failed to find car by plate")
    }

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