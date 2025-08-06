package com.yb.rh.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * Controller handling health check and system status operations
 * Provides system health information for monitoring and debugging
 */
@RestController
@RequestMapping("/api/v1/health")
class HealthController {
    /**
     * Health check endpoint to verify system status
     * @return System health information including status, timestamp, service name, and version
     */
    @GetMapping
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now().toString(),
            "service" to "RushHour Backend",
            "version" to "1.0.0"
        )
    }
} 