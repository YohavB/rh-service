package com.yb.rh.repositories

import com.yb.rh.entities.Car
import org.springframework.data.repository.CrudRepository

interface CarRepository : CrudRepository<Car, String> {
    fun findCarById(carId: Long): Car?
    fun findByPlateNumber(plateNumber: String): Car?
}
