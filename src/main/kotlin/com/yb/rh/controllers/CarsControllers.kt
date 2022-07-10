package com.yb.rh.controllers

import com.yb.rh.entities.*
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
import com.yb.rh.services.CarsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/cars")
class CarsController(
    private val carsService: CarsService
) {

    @GetMapping("/")
    fun findAll() = carsService.findAll()

    @GetMapping("/by_plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String) =
        carsService.findByPlateNumber(plateNumber)

    @PostMapping("/car")
    fun createOrUpdateCar(
        @RequestBody carsDTO: CarsDTO,
        @RequestParam(name = "userId", required = false) userId: Long?
    ) = carsService.createOrUpdateCar(carsDTO, userId)
}
