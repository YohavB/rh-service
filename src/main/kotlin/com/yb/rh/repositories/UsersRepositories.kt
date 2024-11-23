package com.yb.rh.repositories

import com.github.michaelbull.result.*
import com.yb.rh.entities.User
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.RHException
import com.yb.rh.error.SaveDbRecordFailed
import org.springframework.data.repository.CrudRepository

interface UsersRepository : CrudRepository<User, Long> {
    fun findByUserId(id: Long): User?

    fun findByEmail(mail: String): User?
}

fun UsersRepository.saveSafe(user: User): Result<User, RHException> =
    runCatching { save(user) }
        .mapError { SaveDbRecordFailed("users") }

fun UsersRepository.findByUserIdSafe(id: Long): Result<User, RHException> =
    runCatching { findByUserId(id) }
        .mapError { GetDbRecordFailed("users") }
        .andThen { it.toResultOr { EntityNotFound(User::class.java, id.toString()) } }

fun UsersRepository.findByEmailSafe(mail: String): Result<User, RHException> =
    runCatching { findByEmail(mail) }
        .mapError { GetDbRecordFailed("users") }
        .andThen { it.toResultOr { EntityNotFound(User::class.java, mail) } }