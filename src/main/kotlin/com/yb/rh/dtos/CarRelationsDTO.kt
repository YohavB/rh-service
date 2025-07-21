package com.yb.rh.dtos

import com.yb.rh.entities.Car

data class CarRelationsDTO(
    var car: CarDTO,
    var isBlocking: List<CarDTO>,
    var isBlockedBy: List<CarDTO>
) {
    override fun toString(): String {
        return "CarRelationsDTO(car=${car.plateNumber}, isBlocking=${isBlockedBy.map { it.plateNumber }}, isBlockedBy=${isBlocking.map { it.plateNumber }})"
    }
}

data class CarRelations(
    var car: Car,
    var isBlocking: List<Car>,
    var isBlockedBy: List<Car>
) {
    override fun toString(): String {
        return "CarRelations(car=${car.plateNumber}, isBlocking=${isBlockedBy.map { it.plateNumber }}, isBlockedBy=${isBlocking.map { it.plateNumber }})"
    }
}

data class CarsRelationRequestDTO(
    var blockingCarId: Long,
    var blockedCarId: Long,
    var userCarSituation: UserCarSituation
) {
    override fun toString(): String {
        return "CarsRelationsRequestDTO(blockingCarId=$blockingCarId, blockedCarId=$blockedCarId)"
    }
}

enum class UserCarSituation {
    IS_BLOCKING,
    IS_BLOCKED,
}