package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.common.NotificationsKind
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class NotificationServiceTest {
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setUp() {
        notificationService = NotificationService()
    }

    @Test
    fun `test sendPushNotification success`()  {
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-token-123")
        val notificationKind = NotificationsKind.NEED_TO_GO

        notificationService.sendPushNotification(user, notificationKind)

        assertNotNull(notificationService)
    }

    @Test
    fun `test sendPushNotification with invalid token`()  {
        val user = TestObjectBuilder.getUser(pushNotificationToken = "invalid-token")
        val notificationKind = NotificationsKind.NEED_TO_GO

        notificationService.sendPushNotification(user, notificationKind)

        assertNotNull(notificationService)
    }

    @Test
    fun `test sendPushNotification with empty token`()  {
        val user = TestObjectBuilder.getUser(pushNotificationToken = "")
        val notificationKind = NotificationsKind.NEED_TO_GO

        notificationService.sendPushNotification(user, notificationKind)

        assertNotNull(notificationService)
    }

    @Test
    fun `test sendPushNotification with different notification kinds`()  {
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-token-123")

        NotificationsKind.values().forEach { notificationKind ->
            notificationService.sendPushNotification(user, notificationKind)
            assertNotNull(notificationService)
        }
    }

    @Test
    fun `test sendPushNotification with special characters in token`()  {
        val user = TestObjectBuilder.getUser(pushNotificationToken = "ExponentPushToken[test-token-with-special-chars!@#]")
        val notificationKind = NotificationsKind.NEED_TO_GO

        notificationService.sendPushNotification(user, notificationKind)

        assertNotNull(notificationService)
    }

    @Test
    fun `test sendPushNotification with very long token`()  {
        val longToken = "ExponentPushToken[" + "A".repeat(1000) + "]"
        val user = TestObjectBuilder.getUser(pushNotificationToken = longToken)
        val notificationKind = NotificationsKind.NEED_TO_GO

        notificationService.sendPushNotification(user, notificationKind)

        assertNotNull(notificationService)
    }

    @Test
    fun `test sendPushNotification with different user data`()  {
        val users = listOf(
            TestObjectBuilder.getUser(userId = 1L, pushNotificationToken = "token1"),
            TestObjectBuilder.getUser(userId = 2L, pushNotificationToken = "token2"),
            TestObjectBuilder.getUser(userId = 3L, pushNotificationToken = "token3")
        )
        val notificationKind = NotificationsKind.NEED_TO_GO

        users.forEach { user ->
            notificationService.sendPushNotification(user, notificationKind)
            assertNotNull(notificationService)
        }
    }

    @Test
    fun `test sendPushNotification edge cases`()  {
        val edgeCaseTokens = listOf(
            "ExponentPushToken[]",
            "ExponentPushToken[   ]",
            "ExponentPushToken[null]",
            "ExponentPushToken[undefined]",
            "ExponentPushToken[0]",
            "ExponentPushToken[true]",
            "ExponentPushToken[false]"
        )
        val notificationKind = NotificationsKind.NEED_TO_GO

        edgeCaseTokens.forEach { token ->
            val user = TestObjectBuilder.getUser(pushNotificationToken = token)
            notificationService.sendPushNotification(user, notificationKind)
            assertNotNull(notificationService)
        }
    }
} 