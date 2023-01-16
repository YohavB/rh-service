package com.yb.rh.repositories

import com.github.michaelbull.result.*
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.RHException
import com.yb.rh.error.SaveDbRecordFailed
import org.springframework.data.repository.CrudRepository

interface UsersCarsRepository : CrudRepository<UsersCars, Long> {

    fun findByUser(userId: User): List<UsersCars>?

    fun findByCar(car: Car): List<UsersCars>?

    fun findByUserAndCar(userId: User, car: Car): UsersCars

    fun findByBlockingCar(blockingCar: Car): List<UsersCars>?

    fun findByBlockedCar(blockedCar: Car): List<UsersCars>?
}

fun UsersCarsRepository.saveSafe(usersCars: UsersCars): Result<UsersCars, RHException> =
    runCatching { save(usersCars) }
        .mapError { SaveDbRecordFailed("users_cars") }

fun UsersCarsRepository.findByUserSafe(userId: User): Result<List<UsersCars>, RHException> =
    runCatching { findByUser(userId) }
        .mapError { GetDbRecordFailed("users_cars") }
        .andThen { it.toResultOr { EntityNotFound(UsersCars::class.java, userId.userId.toString()) } }

fun UsersCarsRepository.findByCarSafe(car: Car): Result<List<UsersCars>, RHException> =
    runCatching { findByCar(car) }
        .mapError { GetDbRecordFailed("users_cars") }
        .andThen { it.toResultOr { EntityNotFound(UsersCars::class.java, car.plateNumber) } }

fun UsersCarsRepository.findByUserAndCarSafe(userId: User,car: Car): Result<UsersCars, RHException> =
    runCatching { findByUserAndCar(userId,car) }
        .mapError { GetDbRecordFailed("users_cars") }
        .andThen { it.toResultOr { EntityNotFound(UsersCars::class.java, userId.userId.toString()) } }

fun UsersCarsRepository.findByBlockingCarSafe(blockingCar: Car): Result<List<UsersCars>, RHException> =
    runCatching {findByBlockingCar(blockingCar)  }
        .mapError { GetDbRecordFailed("users_cars") }
        .andThen { it.toResultOr { EntityNotFound(UsersCars::class.java, blockingCar.plateNumber) } }

fun UsersCarsRepository.findByBlockedCarSafe(blockedCar: Car): Result<List<UsersCars>, RHException> =
    runCatching { findByBlockedCar(blockedCar) }
        .mapError { GetDbRecordFailed("users_cars") }
        .andThen { it.toResultOr { EntityNotFound(UsersCars::class.java, blockedCar.plateNumber) } }


