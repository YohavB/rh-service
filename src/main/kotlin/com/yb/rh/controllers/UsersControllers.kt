package com.yb.rh.controllers

import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UsersService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(private val usersService: UsersService) : BaseController() {

    @GetMapping("/")
    fun findAll() = usersService.findAll()

    @GetMapping("/by-id")
    fun findById(@RequestParam id: Long): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.findByUserId(id),
            "Successfully found user by ID: $id",
            "Failed to find user by ID: $id"
        )
    }

    @GetMapping("/by-email")
    fun findByEmail(@RequestParam mail: String): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.findByEmail(mail),
            "Successfully found user by email: $mail",
            "Failed to find user by email: $mail"
        )
    }

    @PostMapping("/")
    fun createOrUpdateUser(@RequestBody userDTO: UserDTO): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.createOrUpdateUser(userDTO),
            "Successfully created/updated user: ${userDTO.email}",
            "Failed to create/update user: ${userDTO.email}"
        )
    }
}