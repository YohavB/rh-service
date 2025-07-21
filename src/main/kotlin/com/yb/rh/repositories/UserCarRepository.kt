package com.yb.rh.repositories

import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UserCar
import org.springframework.data.repository.CrudRepository

interface UserCarRepository : CrudRepository<UserCar, Long> {

    fun findAllByUser(user: User): List<UserCar>

    fun findAllByCar(car: Car): List<UserCar>

    fun findByUserAndCar(user: User, car: Car): UserCar?
}


