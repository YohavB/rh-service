package com.yb.rh.controllers

import com.yb.rh.enum.NotificationsKind
import com.yb.rh.services.NotificationService
import com.yb.rh.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * Controller handling health check and system status operations
 * Provides system health information for monitoring and debugging
 */
@RestController
@RequestMapping("/api/v1/health")
class HealthController {

    @Autowired
    private lateinit var notificationService: NotificationService

    @Autowired
    private lateinit var userService: UserService
    /**
     * Health check endpoint to verify system status
     * @return System health information including status, timestamp, service name, and version
     */
    @GetMapping
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().toString(),
            "service" to "RushHour Backend",
            "version" to "1.0.0"
        )
    }

    @GetMapping("/push")
    fun pushNotificationTest(): String {
        val user = userService.getUserById(1)
        notificationService.sendPushNotification(user, NotificationsKind.FREE_TO_GO)
        return "Push notification test endpoint is active"
    }
} 