package com.yb.rh.controllers

import com.yb.rh.dtos.CarRelationsDTO
import com.yb.rh.dtos.CarsRelationRequestDTO
import com.yb.rh.services.MainService
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between cars
 */
@RestController
@RequestMapping("/api/v1/car-relations")
class CarRelationsController(
    private val mainService: MainService,
) : BaseController() {

    @PostMapping
    fun createCarsRelations(
        @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): CarRelationsDTO = mainService.createCarsRelations(carsRelationRequestDTO)

    @GetMapping
    fun getCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    ): CarRelationsDTO = mainService.getCarRelationsByCarId(carId)

    @DeleteMapping
    fun deleteCarRelations(
        @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): CarRelationsDTO = mainService.deleteCarsRelations(carsRelationRequestDTO)

    @DeleteMapping("/all-by-car-id")
    fun deleteAllCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    )= mainService.deleteAllCarRelationsByCarId(carId)
}