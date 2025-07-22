package com.yb.rh.controllers

import com.yb.rh.services.MainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NotificationControllerTest {
    private lateinit var mainService: MainService
    private lateinit var notificationController: NotificationController

    @BeforeEach
    fun setUp() {
        mainService = mockk()
        notificationController = NotificationController(mainService)
    }

    @Test
    fun `test sendNeedToGoNotification success`() {
        // Given
        val blockedCarId = 1L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns "Notification sent successfully"

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification with different car`() {
        // Given
        val blockedCarId = 5L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns "Notification sent successfully"

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification with large car id`() {
        // Given
        val blockedCarId = 999999L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns "Notification sent successfully"

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }
} 