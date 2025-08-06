package com.yb.rh.controllers

import com.yb.rh.dtos.UserDTO
import com.yb.rh.services.UserService
import org.springframework.web.bind.annotation.*

/**
 * Controller handling user-related operations in the Rush Hour service
 * Provides user profile management and account status operations
 */
@RestController
@RequestMapping("/api/v1/user")
class UsersController(private val userService: UserService) {
    /**
     * Get current user profile from JWT token
     * @return Current user's profile information
     * @throws RHException if user is not found or token is invalid
     */
    @GetMapping
    fun getUser(): UserDTO = userService.getUserDTOByToken()

    /**
     * Deactivate current user account
     * @return 200 OK response (user account is marked as inactive)
     * @throws RHException if user is not found or token is invalid
     */
    @PutMapping("/deactivate")
    fun deactivateUserByPath() = userService.deActivateUser()
}