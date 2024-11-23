package com.yb.rh.controllers

import com.yb.rh.common.UserStatus
import com.yb.rh.services.UsersCarsService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users-cars")
class UsersCarsController(
    private val usersCarsService: UsersCarsService,
) : BaseController() {

    @GetMapping("/")
    fun findAll() = usersCarsService.getAllUsersCars()

    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByPlateNumber(plateNumber),
            "Successfully found UsersCars By Plate : $plateNumber",
            "Failed to find UsersCars By Plate : $plateNumber"
        )
    }

    @GetMapping("/by-user")
    fun findByUserId(@RequestParam(name = "userId") userId: Long): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByUserId(userId),
            "Successfully found UsersCars By UserId : $userId",
            "Failed to find UsersCars By UserId : $userId"
        )
    }

    @GetMapping("/by-user-and-plate")
    fun findByUserAndPlate(
        @RequestParam(name = "userId") userId: Long, @RequestParam(name = "plateNumber") plateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByUserAndPlate(userId, plateNumber),
            "Successfully found UsersCars By User :$userId And Plate :$plateNumber",
            "Failed to find UsersCars By User :$userId And Plate :$plateNumber"
        )
    }

    @GetMapping("/blocking")
    fun findBlockingByPlateNumber(
        @RequestParam(name = "blockedPlateNumber") blockedPlateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByBlockedPlateNumber(blockedPlateNumber),
            "Successfully found UsersCars By Blocked Plate :$blockedPlateNumber",
            "Failed to find UsersCars By Blocked Plate :$blockedPlateNumber"
        )
    }

    @GetMapping("/blocked")
    fun findBlockedByPlateNumber(
        @RequestParam(name = "blockingPlateNumber") blockingPlateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByBlockingPlateNumber(blockingPlateNumber),
            "Successfully found UsersCars By Blocking Plate : $blockingPlateNumber",
            "Failed to find UsersCars By Blocking Plate : $blockingPlateNumber"
        )
    }

    @PostMapping("/update-blocked")
    fun updateBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCarPlate") blockingCarPlate: String,
        @RequestParam(name = "blockedCarPlate") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long,
        @RequestParam(name = "userStatus") userStatus: UserStatus,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.updateBlockedCar(blockingCarPlate, blockedCarPlate, userId, userStatus),
            "Successfully updated Blocked Car $blockedCarPlate blocked by Blocking Car : $blockingCarPlate",
            "Failed to update Blocked Car $blockedCarPlate blocked by Blocking Car : $blockingCarPlate"
        )
    }

    @PostMapping("/release-blocked")
    fun releaseBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCarPlate") blockingCarPlate: String,
        @RequestParam(name = "blockedCarPlate") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long,
        @RequestParam(name = "userStatus") userStatus: UserStatus,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.releaseCar(blockingCarPlate, blockedCarPlate, userId, userStatus),
            "Successfully released Blocked Car : $blockedCarPlate blocking by Car : $blockingCarPlate",
            "Failed to release Blocked Car : $blockedCarPlate blocking by Car : $blockingCarPlate"
        )
    }

    @PostMapping("/send-need-to-go-notification")
    fun sendNeedToGoNotification(
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.sendFreeMe(blockedCarPlate),
            "Successfully sent NeedToGo notif for Blocked Car : $blockedCarPlate",
            "Failed to send NeedToGo notif for Blocked Car : $blockedCarPlate"
        )
    }
}