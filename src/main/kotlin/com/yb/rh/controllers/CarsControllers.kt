package com.yb.rh.controllers

import com.yb.rh.services.CarsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cars")
class CarsController(
    private val carsService: CarsService
) {

    @GetMapping("/")
    fun findAll() = carsService.findAll()

    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String) =
        carsService.findByPlateNumber(plateNumber)

    @GetMapping("/carinfo")
    fun getCarInfo(@RequestParam(name = "plateNumber") plateNumber: String) =
        carsService.getCarInfo(plateNumber)

    @PostMapping("/car")
    fun createOrUpdateCar(
        @RequestParam(name = "plateNumber", required = true) plateNumber: String,
        @RequestParam(name = "userId", required = false) userId: Long?
    ) = carsService.createOrUpdateCar(plateNumber, userId)
}
