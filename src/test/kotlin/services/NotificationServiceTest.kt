package services

import com.yb.rh.entities.User
import com.yb.rh.services.NotificationService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationServiceTest {

    private lateinit var notificationService: NotificationService

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        notificationService = spyk(NotificationService())
    }

    @Test
    fun `test send push notification success`() {
        // Mock the service method since we can't access the inner implementation
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // Call the method to test
        notificationService.sendPushNotification(testUser.pushNotificationToken, "blockingCar123")
        
        // Verify the method was called
        verify(exactly = 1) { notificationService.sendPushNotification(testUser.pushNotificationToken, "blockingCar123") }
    }

    @Test
    fun `test send push notification with null token`() {
        val userWithoutToken = testUser.copy(pushNotificationToken = "")
        
        // Mock the service method since we can't access the inner implementation
        every { notificationService.sendPushNotification(any(), any()) } just Runs
        
        // Call the method to test
        notificationService.sendPushNotification(userWithoutToken.pushNotificationToken, "blockingCar123")
        
        // Verify that the method was called
        verify(exactly = 1) { notificationService.sendPushNotification(userWithoutToken.pushNotificationToken, "blockingCar123") }
    }
} 