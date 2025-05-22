package services.simple

import com.yb.rh.services.NotificationService
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

/**
 * Basic test for NotificationService that focuses on minimal functionality without complex mocking
 */
class NotificationServiceBasicTest {

    @Test
    fun `sendPushNotification should not throw exceptions`() {
        // Given
        val notificationService = NotificationService()
        
        // When & Then
        assertDoesNotThrow {
            notificationService.sendPushNotification("test-token", "test-message")
        }
        
        assertDoesNotThrow {
            notificationService.sendPushNotification("", null)
        }
    }
} 