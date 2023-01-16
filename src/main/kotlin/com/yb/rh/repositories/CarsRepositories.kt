package com.yb.rh.repositories

import com.github.michaelbull.result.*
import com.yb.rh.entities.Car
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.RHException
import com.yb.rh.error.SaveDbRecordFailed
import org.springframework.data.repository.CrudRepository

interface CarsRepository : CrudRepository<Car, String> {

    fun findByPlateNumber(plateNumber: String): Car?
}

fun CarsRepository.saveSafe(car: Car): Result<Car, RHException> =
    runCatching { save(car) }
        .mapError { SaveDbRecordFailed("cars") }

fun CarsRepository.findByPlateNumberSafe(plateNumber: String): Result<Car, RHException> =
    runCatching { findByPlateNumber(plateNumber) }
        .mapError { GetDbRecordFailed("cars") }
        .andThen { it.toResultOr { EntityNotFound(Car::class.java, plateNumber) } }
