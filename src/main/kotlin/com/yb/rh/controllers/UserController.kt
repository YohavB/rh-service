package com.yb.rh.controllers

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.services.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import mu.KotlinLogging

/**
 * Controller handling user-related operations in the Rush Hour service
 */
@RestController
@RequestMapping("/api/v1/user")
class UsersController(private val userService: UserService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createUser(
        @Valid @RequestBody userCreationDTO: UserCreationDTO
    ): UserDTO = userService.createUser(userCreationDTO)

    @GetMapping
    fun getUserById(
        @RequestParam(name = "id") id: Long
    ): UserDTO = userService.getUserDTOByUserId(id)

    @GetMapping("/by-email")
    fun getUserByEmail(
        @RequestParam(name = "email") email: String
    ): UserDTO = userService.findUserDTOByEmail(email)

    @PutMapping
    fun updateUser(
        @Valid @RequestBody userDTO: UserDTO
    ): UserDTO = userService.updateUser(userDTO)

    @PutMapping("/deactivate/{userId}")
    fun deactivateUserByPath(
        @PathVariable userId: Long
    ) = userService.deActivateUser(userId)

    @PutMapping("/activate/{userId}")
    fun activateUserByPath(
        @PathVariable userId: Long
    ) = userService.activateUser(userId)
}