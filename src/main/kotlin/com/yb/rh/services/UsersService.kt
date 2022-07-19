package com.yb.rh.services

import com.yb.rh.entities.User
import com.yb.rh.entities.UsersDTO
import com.yb.rh.repositorties.UsersRepository
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UsersService(private val repository: UsersRepository) {
    private val logger = KotlinLogging.logger {}

    fun findAll(): List<User> = repository.findAll().toList()

    fun findByUserId(userId: Long): UsersDTO? {
        logger.info { "Try to find user : $userId " }
        return repository.findByIdOrNull(userId)?.toDto()
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
        return repository.save(User.fromDto(usersDTO)).toDto()
    }
}
