package com.yb.rh.controllers

import com.yb.rh.services.MainService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller handling notification-related operations
 */
@RestController
@RequestMapping("/api/v1/notification")
class NotificationController(
    private val mainService: MainService,
) : BaseController() {

    /**
     * Sends a notification to the blocking car's owner that the blocked car needs to leave
     * @param blockedCarID
     * @return Success/failure response of the notification operation
     */
    @PostMapping("/send-need-to-go")
    fun sendNeedToGoNotification(
        @RequestParam(name = "blockedCarId") blockedCarId: Long
    ) = mainService.sendNeedToGoNotification(blockedCarId)
}