package com.yb.rh.repositories

import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsRelations
import org.springframework.data.repository.CrudRepository

interface CarsRelationsRepository : CrudRepository<CarsRelations, Long> {
    fun findByBlockingCar(car: Car): List<CarsRelations>
    fun findByBlockedCar(car: Car): List<CarsRelations>
}


