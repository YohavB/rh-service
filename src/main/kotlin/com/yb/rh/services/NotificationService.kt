package com.yb.rh.services

import com.yb.rh.entities.User
import com.yb.rh.enum.NotificationsKind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val firebaseService: FirebaseService,
    private val applicationCoroutineScope: CoroutineScope
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Send a push notification to a specific user using FCM
     */
    fun sendPushNotification(user: User, notificationsKind: NotificationsKind) {
        if (!isValidFcmToken(user.pushNotificationToken)) {
            logger.warn { "Invalid FCM token for user ${user.userId}: ${user.pushNotificationToken}" }
            return
        }

        applicationCoroutineScope.launch {
            try {
                val messageId = firebaseService.sendNotificationToToken(
                    token = user.pushNotificationToken,
                    title = notificationsKind.notificationTitle,
                    body = notificationsKind.notificationMessage,
                    data = mapOf(
                        "notificationType" to notificationsKind.name,
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

    /**
     * Send a push notification to multiple users using FCM
     */
    fun sendPushNotificationToUsers(users: List<User>, notificationsKind: NotificationsKind) {
        users.forEach { user -> sendPushNotification(user, notificationsKind) }
    }

    /**
     * Send a notification to a topic (useful for broadcast messages)
     */
    fun sendNotificationToTopic(topic: String, notificationsKind: NotificationsKind) {
        applicationCoroutineScope.launch {
            try {
                val messageId = firebaseService.sendNotificationToTopic(
                    topic = topic,
                    title = notificationsKind.notificationTitle,
                    body = notificationsKind.notificationMessage,
                    data = mapOf(
                        "notificationType" to notificationsKind.name,
                        "topic" to topic,
                        "timestamp" to System.currentTimeMillis().toString()
                    )
                )

                if (messageId != null) {
                    logger.info { "Successfully sent FCM notification to topic '$topic': $messageId" }
                } else {
                    logger.error { "Failed to send FCM notification to topic '$topic'" }
                }
            } catch (e: Exception) {
                logger.error(e) { "Error sending FCM notification to topic '$topic'" }
            }
        }
    }

    /**
     * Subscribe a user's token to a topic
     */
    fun subscribeUserToTopic(user: User, topic: String): Boolean {
        return try {
            if (!isValidFcmToken(user.pushNotificationToken)) {
                logger.warn { "Cannot subscribe invalid FCM token to topic: ${user.pushNotificationToken}" }
                return false
            }

            val success = firebaseService.subscribeToTopic(listOf(user.pushNotificationToken), topic)
            if (success) {
                logger.info { "Successfully subscribed user ${user.userId} to topic '$topic'" }
            } else {
                logger.warn { "Failed to subscribe user ${user.userId} to topic '$topic'" }
            }
            success
        } catch (e: Exception) {
            logger.error(e) { "Error subscribing user ${user.userId} to topic '$topic'" }
            false
        }
    }

    /**
     * Unsubscribe a user's token from a topic
     */
    fun unsubscribeUserFromTopic(user: User, topic: String): Boolean {
        return try {
            if (!isValidFcmToken(user.pushNotificationToken)) {
                logger.warn { "Cannot unsubscribe invalid FCM token from topic: ${user.pushNotificationToken}" }
                return false
            }

            val success = firebaseService.unsubscribeFromTopic(listOf(user.pushNotificationToken), topic)
            if (success) {
                logger.info { "Successfully unsubscribed user ${user.userId} from topic '$topic'" }
            } else {
                logger.warn { "Failed to unsubscribe user ${user.userId} from topic '$topic'" }
            }
            success
        } catch (e: Exception) {
            logger.error(e) { "Error unsubscribing user ${user.userId} from topic '$topic'" }
            false
        }
    }

    /**
     * Validate if an FCM token is valid
     */
    private fun isValidFcmToken(token: String): Boolean {
        return token.isNotBlank() &&
               token.length >= 6 &&
               !token.startsWith("ExponentPushToken[") && // Legacy Expo token format
               !token.startsWith("ExponentPushToken(") && // Alternative Expo token format
               !token.contains("invalid", ignoreCase = true)
    }

    /**
     * Check if a user's FCM token is still valid by attempting to validate it
     */
    fun validateUserToken(user: User): Boolean {
        return try {
            if (!isValidFcmToken(user.pushNotificationToken)) {
                return false
            }
            
            // Note: This will actually send a test message, which might not be desired in production
            // Consider implementing a different validation strategy or removing this method
            firebaseService.isTokenValid(user.pushNotificationToken)
        } catch (e: Exception) {
            logger.error(e) { "Error validating FCM token for user ${user.userId}" }
            false
        }
    }
}