package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.enum.NotificationsKind
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationServiceTest {
    private lateinit var notificationService: NotificationService
    private lateinit var mockFirebaseService: FirebaseService
    private lateinit var testScope: CoroutineScope

    @BeforeEach
    fun setUp() {
        mockFirebaseService = mockk<FirebaseService>(relaxed = true)
        testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        notificationService = NotificationService(mockFirebaseService, testScope)
    }

    @Test
    fun `test sendPushNotification success with valid FCM token`() {
        // Given
        val user = TestObjectBuilder.getUser(userId = 1L, pushNotificationToken = "valid-fcm-token-123")
        val notificationKind = NotificationsKind.NEED_TO_GO
        every { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any()) } returns "message-id-123"

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify { 
            mockFirebaseService.sendNotificationToToken(
                "valid-fcm-token-123",
                "Please move your car",
                "The car you are blocking needs to leave",
                "double_car_horn.wav",
                match { data ->
                    data["notificationType"] == "NEED_TO_GO" &&
                    data["userId"] == "1" &&
                    data.containsKey("timestamp") &&
                    data["timestamp"]?.toLongOrNull() != null
                }
            )
        }
    }

    @Test
    fun `test sendPushNotification with invalid token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "invalid-token")
        val notificationKind = NotificationsKind.NEED_TO_GO

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify(exactly = 0) { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any()) }
    }

    @Test
    fun `test sendPushNotification with empty token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "")
        val notificationKind = NotificationsKind.NEED_TO_GO

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify(exactly = 0) { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any()) }
    }

    @Test
    fun `test sendPushNotification with legacy Expo token format`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "ExponentPushToken[test-token]")
        val notificationKind = NotificationsKind.NEED_TO_GO

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify(exactly = 0) { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any()) }
    }

    @Test
    fun `test sendPushNotification with different notification kinds`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-fcm-token-123")
        every { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any()) } returns "message-id"

        // When
        NotificationsKind.entries.forEach { notificationKind ->
            notificationService.sendPushNotification(user, notificationKind)
        }

        // Then
        verify(exactly = NotificationsKind.entries.size) {
            mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `test sendPushNotificationToUsers with multiple users`() {
        // Given
        val users = listOf(
            TestObjectBuilder.getUser(userId = 1L, pushNotificationToken = "token1"),
            TestObjectBuilder.getUser(userId = 2L, pushNotificationToken = "token2"),
            TestObjectBuilder.getUser(userId = 3L, pushNotificationToken = "token3")
        )
        val notificationKind = NotificationsKind.NEED_TO_GO
        every { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any()) } returns "message-id"

        // When
        notificationService.sendPushNotificationToUsers(users, notificationKind)

        // Then
        verify(exactly = 3) {
            mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `test sendNotificationToTopic success`() {
        // Given
        val topic = "general"
        val notificationKind = NotificationsKind.FREE_TO_GO
        every { mockFirebaseService.sendNotificationToTopic(any(), any(), any(), any()) } returns "message-id-123"

        // When
        notificationService.sendNotificationToTopic(topic, notificationKind)

        // Then
        verify { 
            mockFirebaseService.sendNotificationToTopic(
                "general",
                "You are free to go",
                "No car is blocking you anymore",
                "car_horn.wav",
                match { data ->
                    data["notificationType"] == "FREE_TO_GO" &&
                    data["topic"] == "general" &&
                    data.containsKey("timestamp") &&
                    data["timestamp"]?.toLongOrNull() != null
                }
            )
        }
    }

    @Test
    fun `test subscribeUserToTopic success`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-fcm-token")
        val topic = "parking-updates"
        every { mockFirebaseService.subscribeToTopic(any(), any()) } returns true

        // When
        val result = notificationService.subscribeUserToTopic(user, topic)

        // Then
        assertTrue(result)
        verify { mockFirebaseService.subscribeToTopic(listOf("valid-fcm-token"), "parking-updates") }
    }

    @Test
    fun `test subscribeUserToTopic with invalid token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "invalid-token")
        val topic = "parking-updates"

        // When
        val result = notificationService.subscribeUserToTopic(user, topic)

        // Then
        assertFalse(result)
        verify(exactly = 0) { mockFirebaseService.subscribeToTopic(any(), any()) }
    }

    @Test
    fun `test unsubscribeUserFromTopic success`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-fcm-token")
        val topic = "parking-updates"
        every { mockFirebaseService.unsubscribeFromTopic(any(), any()) } returns true

        // When
        val result = notificationService.unsubscribeUserFromTopic(user, topic)

        // Then
        assertTrue(result)
        verify { mockFirebaseService.unsubscribeFromTopic(listOf("valid-fcm-token"), "parking-updates") }
    }

    @Test
    fun `test validateUserToken with valid token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-fcm-token")
        every { mockFirebaseService.isTokenValid(any()) } returns true

        // When
        val result = notificationService.validateUserToken(user)

        // Then
        assertTrue(result)
        verify { mockFirebaseService.isTokenValid("valid-fcm-token") }
    }

    @Test
    fun `test validateUserToken with invalid token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "invalid-token")

        // When
        val result = notificationService.validateUserToken(user)

        // Then
        assertFalse(result)
        verify(exactly = 0) { mockFirebaseService.isTokenValid(any()) }
    }

    @Test
    fun `test sendPushNotification when Firebase service fails`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "valid-fcm-token-123")
        val notificationKind = NotificationsKind.BEEN_BLOCKED
        every { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any()) } returns null

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify {
            mockFirebaseService.sendNotificationToToken(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `test sendPushNotification with very short token`() {
        // Given
        val user = TestObjectBuilder.getUser(pushNotificationToken = "short")
        val notificationKind = NotificationsKind.NEED_TO_GO

        // When
        notificationService.sendPushNotification(user, notificationKind)

        // Then
        verify(exactly = 0) { mockFirebaseService.sendNotificationToToken(any(), any(), any(), any()) }
    }
} 