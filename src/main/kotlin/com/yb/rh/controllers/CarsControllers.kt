package com.yb.rh.controllers

import com.github.michaelbull.result.*
import com.yb.rh.services.CarService
import com.yb.rh.utils.RHResponse
import com.yb.rh.utils.SuccessResponse
import com.yb.rh.utils.Utils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cars")
class CarsController(
    private val carService: CarService,
) {

    var logger = KotlinLogging.logger {}

    @GetMapping("/")
    fun findAll() = carService.findAll()

    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): ResponseEntity<out RHResponse> {
        return carService.findByPlateNumber(plateNumber)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @PostMapping("/car")
    fun createOrUpdateCar(
        @RequestParam(name = "plateNumber", required = true) plateNumber: String,
        @RequestParam(name = "userId", required = false) userId: Long?,
    ): ResponseEntity<out RHResponse> {
        return carService.createOrUpdateCar(plateNumber, userId)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


//    @GetMapping("/carinfo")
//    fun getCarInfo(@RequestParam(name = "plateNumber") plateNumber: String) =
//        carsService.getCarInfo(plateNumber)
}
