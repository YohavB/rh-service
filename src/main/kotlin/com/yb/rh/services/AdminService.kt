package com.yb.rh.services

import com.yb.rh.dtos.*
import com.yb.rh.enum.NotificationsKind
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val userService: UserService,
    private val firebaseService: FirebaseService,
) {
    private val logger = KotlinLogging.logger {}

    fun sendPushNotifications(adminPushNotificationDTO: AdminPushNotificationDTO) {
        logger.info { "Admin requested to send push notification to users" }

        val user = userService.getUserById(adminPushNotificationDTO.userId)

        try {
            val messageId = firebaseService.sendNotificationToToken(
                token = user.pushNotificationToken,
                title = adminPushNotificationDTO.title,
                body = adminPushNotificationDTO.body,
                sound = adminPushNotificationDTO.sound.soundFileName,
                data = mapOf(
                    "notificationType" to "admin_message",
                    "userId" to user.userId.toString(),
                    "timestamp" to System.currentTimeMillis().toString()
                )
            )

            if (messageId != null) {
                logger.info { "Successfully sent FCM notification to user ${user.userId}: $messageId" }
            } else {
                logger.error { "Failed to send FCM notification to user ${user.userId}" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error sending FCM notification to user ${user.userId}" }
        }

}
}
