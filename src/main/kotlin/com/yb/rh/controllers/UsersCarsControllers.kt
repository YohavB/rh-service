package com.yb.rh.controllers

import com.yb.rh.entities.CarsDTO
import com.yb.rh.entities.UsersDTO
import com.yb.rh.services.UsersCarsService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/users-cars")
class UsersCarsController(
    private val usersCarsService: UsersCarsService
) {

    @GetMapping("/")
    fun findAll() = usersCarsService.getAllUsersCars()


    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String) =
        usersCarsService.getUsersCarsByPlateNumber(plateNumber)


    @GetMapping("/by-user")
    fun findByUserId(@RequestParam(name = "userId") userId: Long) =
        usersCarsService.getCarsByUserId(userId)


    @GetMapping("/blocking")
    fun findBlockingByPlateNumber(@RequestParam(name = "blockedPlateNumber") blockedPlateNumber: String) =
        usersCarsService.getBlockingCarByBlockedPlateNumber(blockedPlateNumber)


    @GetMapping("/blocked")
    fun findBlockedByPlateNumber(@RequestParam(name = "blockingPlateNumber") blockingPlateNumber: String) =
        usersCarsService.getBlockedCarByBlockingPlateNumber(blockingPlateNumber)


    @PostMapping("/update-blocking")
    fun updateBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCar") blockingCarPlate: String,
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
        @RequestParam(name = "user") userDto: UsersDTO
    ) = usersCarsService.updateBlockedCar(blockingCarPlate, blockedCarPlate, userDto)


    @PostMapping("/release-blocking")
    fun releaseBlockedCarByPlateNumber(
        @RequestParam blockingCarDTO: CarsDTO,
        @RequestParam blockedCarDTO: CarsDTO,
        @RequestParam userDto: UsersDTO
    ) {
        usersCarsService.releaseCar(blockingCarDTO, blockedCarDTO, userDto)
    }
}
