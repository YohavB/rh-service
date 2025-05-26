package com.yb.rh.controllers

import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UsersService
import com.yb.rh.utils.RHResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller handling user-related operations in the Rush Hour service
 */
@RestController
@RequestMapping("/api/users")
class UsersController(private val usersService: UsersService) : BaseController() {

    /**
     * Retrieves all users registered in the system
     * @return List of all users
     */
    @GetMapping("/")
    fun findAll() = usersService.findAll()

    /**
     * Finds a user by their unique identifier
     * @param id The unique identifier of the user
     * @return The user details if found
     */
    @GetMapping("/by-id")
    fun findById(@RequestParam id: Long): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.findByUserId(id),
            "Successfully found user by ID: $id",
            "Failed to find user by ID: $id"
        )
    }

    /**
     * Finds a user by their email address
     * @param mail The email address of the user
     * @return The user details if found
     */
    @GetMapping("/by-email")
    fun findByEmail(@RequestParam mail: String): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.findByEmail(mail),
            "Successfully found user by email: $mail",
            "Failed to find user by email: $mail"
        )
    }

    /**
     * Creates a new user or updates an existing one
     * @param userDTO The user data transfer object containing user details
     * @return The created or updated user details
     */
    @PostMapping("/")
    fun createOrUpdateUser(@RequestBody userDTO: UserDTO): ResponseEntity<out RHResponse> {
        return handleServiceResult(
            usersService.createOrUpdateUser(userDTO),
            "Successfully created/updated user: ${userDTO.email}",
            "Failed to create/update user: ${userDTO.email}"
        )
    }
}