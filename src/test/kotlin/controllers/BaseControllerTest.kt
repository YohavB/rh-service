package com.yb.rh.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class BaseControllerTest {
    private lateinit var baseController: BaseController

    @BeforeEach
    fun setUp() {
        baseController = BaseController()
    }

    @Test
    fun `test logger is accessible`() {
        // Given & When
        val logger = baseController.logger

        // Then
        assertNotNull(logger)
    }

    @Test
    fun `test BaseController can be instantiated`() {
        // Given & When & Then
        assertNotNull(baseController)
    }
} 