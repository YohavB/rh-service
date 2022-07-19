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

    @GetMapping("/by-user-and-plate")
    fun findByUserAndPlate(@RequestParam (name = "userId") userId: Long
    ,@RequestParam(name = "plateNumber") plateNumber: String) = usersCarsService.getByUserAndPlate(userId,plateNumber)


    @GetMapping("/blocking")
    fun findBlockingByPlateNumber(@RequestParam(name = "blockedPlateNumber") blockedPlateNumber: String) =
        usersCarsService.getBlockingCarByBlockedPlateNumber(blockedPlateNumber)


    @GetMapping("/blocked")
    fun findBlockedByPlateNumber(@RequestParam(name = "blockingPlateNumber") blockingPlateNumber: String) =
        usersCarsService.getBlockedCarByBlockingPlateNumber(blockingPlateNumber)


    @PostMapping("/update-blocked")
    fun updateBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCar") blockingCarPlate: String,
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long
    ) = usersCarsService.updateBlockedCar(blockingCarPlate, blockedCarPlate, userId)


    @PostMapping("/release-blocked")
    fun releaseBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCar") blockingCarPlate: String,
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long
    ) {
        usersCarsService.releaseCar(blockingCarPlate, blockedCarPlate, userId)
    }
}
