package com.yb.rh.controllers

import com.yb.rh.dtos.AuthResponseDTO
import com.yb.rh.dtos.OAuthLoginRequestDTO
import com.yb.rh.enum.OAuthProvider
import com.yb.rh.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/google")
    fun googleLogin(@RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.GOOGLE)
        return ResponseEntity.ok(result)
    }
    
    @PostMapping("/facebook")
    fun facebookLogin(@Valid @RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.FACEBOOK)
        return ResponseEntity.ok(result)
    }
    
    @PostMapping("/apple")
    fun appleLogin(@Valid @RequestBody request: OAuthLoginRequestDTO): ResponseEntity<AuthResponseDTO> {
        val result = authService.login(request, OAuthProvider.APPLE)
        return ResponseEntity.ok(result)
    }
    
    @PostMapping("/refresh")
    fun refreshToken(): ResponseEntity<AuthResponseDTO> {
        val result = authService.refreshToken()
        return ResponseEntity.ok(result)
    }
    
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        val result = authService.logout()
        return ResponseEntity.ok(result)
    }
} 