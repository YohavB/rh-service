package com.yb.rh.controllers

import com.yb.rh.dtos.AdminPushNotificationDTO
import com.yb.rh.services.AdminService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService
) {
    @GetMapping("/health")
    fun adminCheck(): String {
        return "Admin access confirmed"
    }

    @PostMapping("/push-notifications")
    fun sendPushNotifications(@RequestBody adminPushNotificationDTO: AdminPushNotificationDTO) {
        adminService.sendPushNotifications(adminPushNotificationDTO)
    }
}