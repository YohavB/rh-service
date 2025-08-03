package com.yb.rh.controllers

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HealthControllerTest {
    private lateinit var healthController: HealthController

    @BeforeEach
    fun setUp() {
        healthController = HealthController()
    }

    @Test
    fun `test health endpoint returns correct response`() {
        // When
        val result = healthController.health()

        // Then
        assertNotNull(result)
        assertEquals("UP", result["status"])
        assertEquals("RushHour Backend", result["service"])
        assertEquals("1.0.0", result["version"])
        assertNotNull(result["timestamp"])
    }

    @Test
    fun `test health endpoint returns all required fields`() {
        // When
        val result = healthController.health()

        // Then
        assertNotNull(result)
        assertEquals(4, result.size)
        assertNotNull(result["status"])
        assertNotNull(result["timestamp"])
        assertNotNull(result["service"])
        assertNotNull(result["version"])
    }

    @Test
    fun `test health endpoint timestamp is not empty`() {
        // When
        val result = healthController.health()

        // Then
        assertNotNull(result)
        val timestamp = result["timestamp"] as String
        assertNotNull(timestamp)
        assert(timestamp.isNotEmpty())
    }
} 