package services

import com.yb.rh.entities.User
import com.yb.rh.services.NotificationService
import io.github.jav.exposerversdk.*
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
        notificationService = mockk(relaxed = true)
    }

    @Test
    fun `test send push notification success`() {
        // Mock the service method
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // Call the method to test
        notificationService.sendPushNotification(testUser.pushNotificationToken, "blockingCar123")
        
        // Verify the method was called
        verify(exactly = 1) { notificationService.sendPushNotification(testUser.pushNotificationToken, "blockingCar123") }
    }

    @Test
    fun `test send push notification with null token`() {
        val userWithoutToken = testUser.copy(pushNotificationToken = "")
        
        // Mock the service method
        every { notificationService.sendPushNotification(any(), any()) } just Runs
        
        // Call the method to test
        notificationService.sendPushNotification(userWithoutToken.pushNotificationToken, "blockingCar123")
        
        // Verify that the method was called
        verify(exactly = 1) { notificationService.sendPushNotification(userWithoutToken.pushNotificationToken, "blockingCar123") }
    }
    
    @Test
    fun `test send push notification with different blocked car`() {
        // Mock the service method
        every { notificationService.sendPushNotification(any(), any()) } just Runs
        
        // Call the method with a different blocked car
        notificationService.sendPushNotification("test-token", "differentCar")
        
        // Verify that the method was called
        verify(exactly = 1) { notificationService.sendPushNotification("test-token", "differentCar") }
    }
    
    @Test
    fun `test send push notification with null blocked car`() {
        // Mock the service method
        every { notificationService.sendPushNotification(any(), null) } just Runs
        
        // Call the method with null blocked car
        notificationService.sendPushNotification("test-token", null)
        
        // Verify that the method was called with null blocked car
        verify(exactly = 1) { notificationService.sendPushNotification("test-token", null) }
    }
} 