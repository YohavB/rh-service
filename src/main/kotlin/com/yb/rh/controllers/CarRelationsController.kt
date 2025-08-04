package com.yb.rh.controllers

import com.yb.rh.dtos.CarRelationsDTO
import com.yb.rh.dtos.CarsRelationRequestDTO
import com.yb.rh.services.MainService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between cars
 */
@RestController
@RequestMapping("/api/v1/car-relations")
class CarRelationsController(
    private val mainService: MainService,
) {
    @PostMapping
    fun createCarsRelations(
        @Valid @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): CarRelationsDTO = mainService.createCarsRelations(carsRelationRequestDTO)

    @GetMapping("/by-car-id")
    fun getCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    ): CarRelationsDTO = mainService.getCarRelationsByCarId(carId)

    @GetMapping
    fun getUserCarRelations(): List<CarRelationsDTO> = mainService.getUserCarRelations()

    @DeleteMapping
    fun deleteCarRelations(
        @Valid @RequestBody carsRelationRequestDTO: CarsRelationRequestDTO
    ): CarRelationsDTO = mainService.deleteCarsRelations(carsRelationRequestDTO)

    @DeleteMapping("/all-by-car-id")
    fun deleteAllCarRelationsByCarId(
        @RequestParam(name = "carId") carId: Long
    )= mainService.deleteAllCarRelationsByCarId(carId)
}