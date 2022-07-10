package com.yb.rh.repositorties

import com.yb.rh.entities.Cars
import com.yb.rh.entities.Users
import com.yb.rh.entities.UsersCars
import org.springframework.data.repository.CrudRepository

interface UsersRepository : CrudRepository<Users, Long> {
    fun findByUserId(id: Long): Users?
    fun findByMail(mail: String): Users?
    fun findByPhone(phone: String): Users?
}

interface CarsRepository : CrudRepository<Cars, Long> {
    fun findByPlateNumber(plateNumber: String): Cars?
}

interface UsersCarsRepository : CrudRepository<UsersCars, Long> {
    fun findByUserId(userId: Users): List<UsersCars>?
    fun findByUserCar(car: Cars): List<UsersCars>?
    fun findByUserIdAndUserCar(userId: Users, car: Cars): UsersCars?
    fun findBlockedCarsByBlockingCar(blockingCar: Cars): List<UsersCars>?
    fun findBlockingCarsByBlockedCar(blockedCar: Cars): List<UsersCars>?
}
