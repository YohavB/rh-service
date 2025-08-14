package com.yb.rh.controllers

import com.yb.rh.services.MainService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns Unit

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification with zero car id`() {
        // Given
        val blockedCarId = 0L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns Unit

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification with large car id`() {
        // Given
        val blockedCarId = 999999L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns Unit

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification returns empty message`() {
        // Given
        val blockedCarId = 1L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns Unit

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }

    @Test
    fun `test sendNeedToGoNotification with negative car id`() {
        // Given
        val blockedCarId = -1L
        
        every { mainService.sendNeedToGoNotification(blockedCarId) } returns Unit

        // When
        notificationController.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { mainService.sendNeedToGoNotification(blockedCarId) }
    }
} 