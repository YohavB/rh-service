package com.yb.rh.repositorties

import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import org.springframework.data.repository.CrudRepository

interface UsersRepository : CrudRepository<User, Long> {
    fun findByUserId(id: Long): User
    fun findByMail(mail: String): User?
    fun findByPhone(phone: String): User?
}

interface CarsRepository : CrudRepository<Car, String> {
    fun findByPlateNumber(plateNumber: String): Car?
}

interface UsersCarsRepository : CrudRepository<UsersCars, Long> {
    fun findByUser(userId: User): List<UsersCars>?
    fun findByCar(car: Car): List<UsersCars>?
    fun findByUserAndCar(userId: User, car: Car): UsersCars
    fun findByBlockingCar(blockingCar: Car): List<UsersCars>?
    fun findByBlockedCar(blockedCar: Car): List<UsersCars>?
}
