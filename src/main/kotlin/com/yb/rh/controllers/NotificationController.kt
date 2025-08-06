package com.yb.rh.controllers

import com.yb.rh.services.MainService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller handling notification-related operations
 * Provides manual notification sending for car blocking scenarios
 */
@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val mainService: MainService,
) {
    /**
     * Send manual "need to go" notification for a blocked car
     * Sends a notification to the owner of the blocking car that the blocked car needs to leave
     * This is a manual trigger for notifications that would normally be sent automatically
     * 
     * @param blockedCarId ID of the car that is blocked and needs to leave
     * @return Success response indicating notification was sent
     * @throws RHException if car is not found, has no owner, or is not blocked by any car
     */
    @PostMapping("/send-need-to-go")
    fun sendNeedToGoNotification(
        @RequestParam(name = "blockedCarId") blockedCarId: Long
    ) = mainService.sendNeedToGoNotification(blockedCarId)
}