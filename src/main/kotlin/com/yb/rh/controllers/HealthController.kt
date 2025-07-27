package com.yb.rh.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import mu.KotlinLogging

@RestController
@RequestMapping("/api/v1/health")
class HealthController {
    private val logger = KotlinLogging.logger {}

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