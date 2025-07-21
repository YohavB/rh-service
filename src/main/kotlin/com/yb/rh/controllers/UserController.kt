package com.yb.rh.controllers

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.services.UserService
import org.springframework.web.bind.annotation.*

/**
 * Controller handling user-related operations in the Rush Hour service
 */
@RestController
@RequestMapping("/api/v1/user")
class UsersController(private val userService: UserService) : BaseController() {

    @PostMapping
    fun createUser(
        @RequestBody userCreationDTO: UserCreationDTO
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
        @RequestBody userDTO: UserDTO
    ): UserDTO = userService.updateUser(userDTO)

    @PutMapping("/deactivate")
    fun deactivateUser(
        @RequestParam(name = "id") id: Long
    ) = userService.deActivateUser(id)

    @PutMapping("/activate")
    fun activateUser(
        @RequestParam(name = "id") id: Long
    ) = userService.activateUser(id)
}