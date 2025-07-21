package com.yb.rh.repositories

import com.yb.rh.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByUserId(id: Long): User?

    fun findByEmail(mail: String): User?
}