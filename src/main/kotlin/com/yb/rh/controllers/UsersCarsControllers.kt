package com.yb.rh.controllers

import com.github.michaelbull.result.*
import com.yb.rh.common.UserStatus
import com.yb.rh.services.UsersCarsService
import com.yb.rh.utils.RHResponse
import com.yb.rh.utils.SuccessResponse
import com.yb.rh.utils.Utils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/users-cars")
class UsersCarsController(
    private val usersCarsService: UsersCarsService,
) {

    var logger = KotlinLogging.logger {}

    @GetMapping("/")
    fun findAll() = usersCarsService.getAllUsersCars()

    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): ResponseEntity<out RHResponse> {
        return usersCarsService.getUsersCarsByPlateNumber(plateNumber)
            .onSuccess { logger.info { "Successfully find UsersCars By Plate : $plateNumber" } }
            .onFailure { logger.warn(it) { "Failed to find UsersCars By Plate : $plateNumber" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


    @GetMapping("/by-user")
    fun findByUserId(@RequestParam(name = "userId") userId: Long): ResponseEntity<out RHResponse> {
        return usersCarsService.getUsersCarsByUserId(userId)
            .onSuccess { logger.info { "Successfully find UsersCars By UserId : $userId" } }
            .onFailure { logger.warn(it) { "Failed to find UsersCars By UserId : $userId" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


    @GetMapping("/by-user-and-plate")
    fun findByUserAndPlate(
        @RequestParam(name = "userId") userId: Long, @RequestParam(name = "plateNumber") plateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.getUsersCarsByUserAndPlate(userId, plateNumber)
            .onSuccess { logger.info { "Successfully find UsersCars By User :$userId And Plate :$plateNumber" } }
            .onFailure { logger.warn(it) { "Failed to find UsersCars By User :$userId And Plate :$plateNumber" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


    @GetMapping("/blocking")
    fun findBlockingByPlateNumber(
        @RequestParam(name = "blockedPlateNumber") blockedPlateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.getUsersCarsByBlockedPlateNumber(blockedPlateNumber)
            .onSuccess { logger.info { "Successfully find UsersCars By Blocked Plate :$blockedPlateNumber" } }
            .onFailure { logger.warn(it) { "Failed to find UsersCars By Blocked Plate :$blockedPlateNumber" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


    @GetMapping("/blocked")
    fun findBlockedByPlateNumber(
        @RequestParam(name = "blockingPlateNumber") blockingPlateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.getUsersCarsByBlockingPlateNumber(blockingPlateNumber)
            .onSuccess { logger.info { "Successfully find UsersCars By Blocking Plate : $blockingPlateNumber" } }
            .onFailure { logger.warn(it) { "Failed to find UsersCars By Blocking Plate : $blockingPlateNumber" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }


    @PostMapping("/update-blocked")
    fun updateBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCar") blockingCarPlate: String,
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long,
        @RequestParam(name = "userStatus") userStatus: UserStatus,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.updateBlockedCar(blockingCarPlate, blockedCarPlate, userId, userStatus)
            .onSuccess { logger.info { "Successfully update Blocked Car $blockedCarPlate blocked by Blocking Car : $blockingCarPlate" } }
            .onFailure { logger.warn(it) { "Failed to update Blocked Car $blockedCarPlate blocked by Blocking Car : $blockingCarPlate" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @PostMapping("/release-blocked")
    fun releaseBlockedCarByPlateNumber(
        @RequestParam(name = "blockingCar") blockingCarPlate: String,
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
        @RequestParam(name = "userId") userId: Long,
        @RequestParam(name = "userStatus") userStatus: UserStatus,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.releaseCar(blockingCarPlate, blockedCarPlate, userId, userStatus)
            .onSuccess { logger.info { "Successfully released Blocked Car : $blockedCarPlate blocking by Car : $blockingCarPlate" } }
            .onFailure { logger.warn(it) { "Failed to released Blocked Car : $blockedCarPlate blocking by Car : $blockingCarPlate" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @PostMapping("/send-need-to-go-notification")
    fun sendNeedToGoNotification(
        @RequestParam(name = "blockedCar") blockedCarPlate: String,
    ): ResponseEntity<out RHResponse> {
        return usersCarsService.sendFreeMe(blockedCarPlate)
            .onSuccess { logger.info { "Successfully sent NeedToGo notif for Blocked Car : $blockedCarPlate" } }
            .onFailure { logger.warn(it) { "Failed to sent NeedToGo notif for Blocked Car : $blockedCarPlate" } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }
}
