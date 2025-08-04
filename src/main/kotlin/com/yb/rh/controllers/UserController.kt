package com.yb.rh.controllers

import com.yb.rh.dtos.UserDTO
import com.yb.rh.services.UserService
import org.springframework.web.bind.annotation.*

/**
 * Controller handling user-related operations in the Rush Hour service
 */
@RestController
@RequestMapping("/api/v1/user")
class UsersController(private val userService: UserService) {
    @GetMapping
    fun getUser(): UserDTO = userService.getUserDTOByToken()

    @PutMapping("/deactivate/{userId}")
    fun deactivateUserByPath(
        @PathVariable userId: Long
    ) = userService.deActivateUser(userId)

    @PutMapping("/activate/{userId}")
    fun activateUserByPath(
        @PathVariable userId: Long
    ) = userService.activateUser(userId)
}