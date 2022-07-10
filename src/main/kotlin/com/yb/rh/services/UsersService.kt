package com.yb.rh.services

import com.yb.rh.entities.Users
import com.yb.rh.entities.UsersDTO
import com.yb.rh.repositorties.UsersRepository
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UsersService(private val repository: UsersRepository) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<Users> = repository.findAll().toList()

    fun findById(userId: Long): UsersDTO? {
        logger.info { "Try to find user : $userId " }
        return repository.findByUserId(userId)?.toDto()
    }

    fun findByMail(mail: String): UsersDTO? {
        logger.info { "Try to find user by mail : $mail " }
        return repository.findByMail(mail)?.toDto()
    }

    fun findByPhone(phone: String): UsersDTO? {
        logger.info { "Try to find user by phone: $phone " }
        return repository.findByPhone(phone)?.toDto()
    }

    fun createOrUpdateUser(usersDTO: UsersDTO): UsersDTO? {
        logger.info { "Create or update user : ${usersDTO.mail} " }
        var currentUser = repository.findByMail(usersDTO.mail)
        if (currentUser != null) {
            currentUser.firstName = usersDTO.firstName
            currentUser.lastName = usersDTO.lastName
        } else {
            currentUser = Users.fromDto(usersDTO)
        }
        return repository.save(currentUser).toDto()
    }
}
