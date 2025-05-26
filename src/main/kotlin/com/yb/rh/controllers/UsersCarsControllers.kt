package com.yb.rh.controllers

import com.yb.rh.common.UserStatus
import com.yb.rh.services.UsersCarsService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller handling relationships between users and cars in the Rush Hour service
 */
@RestController
@RequestMapping("/api/users-cars")
class UsersCarsController(
    private val usersCarsService: UsersCarsService,
) : BaseController() {

    /**
     * Retrieves all user-car relationships in the system
     * @return List of all user-car associations
     */
    @GetMapping("/")
    fun findAll() = usersCarsService.getAllUsersCars()

    /**
     * Finds user-car relationships by car plate number
     * @param plateNumber The license plate number of the car
     * @return List of user-car relationships associated with the plate number
     */
    @GetMapping("/by-plate")
    fun findByPlateNumber(@RequestParam(name = "plateNumber") plateNumber: String): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByPlateNumber(plateNumber),
            "Successfully found UsersCars By Plate : $plateNumber",
            "Failed to find UsersCars By Plate : $plateNumber"
        )
    }

    /**
     * Finds user-car relationships by user ID
     * @param userId The unique identifier of the user
     * @return List of user-car relationships associated with the user
     */
    @GetMapping("/by-user")
    fun findByUserId(@RequestParam(name = "userId") userId: Long): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByUserId(userId),
            "Successfully found UsersCars By UserId : $userId",
            "Failed to find UsersCars By UserId : $userId"
        )
    }

    /**
     * Finds user-car relationship by both user ID and car plate number
     * @param userId The unique identifier of the user
     * @param plateNumber The license plate number of the car
     * @return The specific user-car relationship if found
     */
    @GetMapping("/by-user-and-plate")
    fun findByUserAndPlate(
        @RequestParam(name = "userId") userId: Long, 
        @RequestParam(name = "plateNumber") plateNumber: String,
    ): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersCarsService.getUsersCarsByUserAndPlate(userId, plateNumber),
            "Successfully found UsersCars By User :$userId And Plate :$plateNumber",
            "Failed to find UsersCars By User :$userId And Plate :$plateNumber"
        )
    }

    /**
     * Finds cars that are blocking a specific car
     * @param blockedPlateNumber The license plate number of the blocked car
     * @return List of user-car relationships where cars are blocking the specified car
     */
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

    /**
     * Finds cars that are being blocked by a specific car
     * @param blockingPlateNumber The license plate number of the blocking car
     * @return List of user-car relationships where cars are being blocked by the specified car
     */
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

    /**
     * Updates the blocking relationship between two cars
     * @param blockingCarPlate The license plate number of the car that is blocking
     * @param blockedCarPlate The license plate number of the car being blocked
     * @param userId The user ID associated with the blocking car
     * @param userStatus The status of the user in this blocking relationship
     * @return Success/failure response of the update operation
     */
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

    /**
     * Releases a blocked car from its blocking relationship
     * @param blockingCarPlate The license plate number of the car that is blocking
     * @param blockedCarPlate The license plate number of the car being blocked
     * @param userId The user ID associated with the blocking car
     * @param userStatus The status of the user in this blocking relationship
     * @return Success/failure response of the release operation
     */
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

    /**
     * Sends a notification to the blocking car's owner that the blocked car needs to leave
     * @param blockedCarPlate The license plate number of the blocked car
     * @return Success/failure response of the notification operation
     */
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