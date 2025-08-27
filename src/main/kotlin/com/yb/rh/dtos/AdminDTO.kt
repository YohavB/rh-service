package com.yb.rh.dtos

import com.yb.rh.enum.NotificationsSound

data class AdminPushNotificationDTO(
    val userId: Long,
    val title: String,
    val body: String,
    val sound: NotificationsSound = NotificationsSound.DEFAULT
)