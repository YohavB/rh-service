package com.yb.rh.controllers

import com.github.michaelbull.result.*
import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UsersService
import com.yb.rh.utils.RHResponse
import com.yb.rh.utils.SuccessResponse
import com.yb.rh.utils.Utils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(private val usersService: UsersService) {

    var logger = KotlinLogging.logger {}

    @GetMapping("/")
    fun findAll() = usersService.findAll()

    @GetMapping("/id")
    fun findById(@RequestParam id: Long): ResponseEntity<out RHResponse> {
        return usersService.findByUserId(id)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @GetMapping("/by-mail")
    fun findByEmail(@RequestParam mail: String): ResponseEntity<out RHResponse> {
        return usersService.findByEmail(mail)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @GetMapping("/by-external-id")
    fun findByExternalId(@RequestParam externalId: String): ResponseEntity<out RHResponse> {
        return usersService.findByExternalId(externalId)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }

    @PostMapping("/")
    fun createOrUpdateUser(@RequestBody userDTO: UserDTO): ResponseEntity<out RHResponse> {
        return usersService.createOrUpdateUser(userDTO)
            .onSuccess { logger.info { "Successfully " } }
            .onFailure { logger.warn(it) { "Failed  " } }
            .mapError { Utils.mapRHErrorToResponse(it) }
            .map { ResponseEntity.ok(SuccessResponse(it)) }
            .fold({ it }, { it })
    }
}