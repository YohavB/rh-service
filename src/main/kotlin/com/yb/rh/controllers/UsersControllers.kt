package com.yb.rh.controllers

import com.yb.rh.entities.UsersDTO
import com.yb.rh.services.UsersService
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UsersController(private val usersService: UsersService) {

    @GetMapping("/")
    fun findAll() = usersService.findAll()

    @GetMapping("/id")
    fun findById(@RequestParam id: Long) = usersService.findById(id)

    @GetMapping("/by-mail")
    fun findByMail(@RequestParam mail: String) = usersService.findByMail(mail)

    @GetMapping("/by-phone")
    fun findByPhone(@RequestParam phone: String) = usersService.findByPhone(phone)

    @PostMapping("/")
    fun createOrUpdateUser(@RequestBody usersDTO: UsersDTO) = usersService.createOrUpdateUser(usersDTO)
}
