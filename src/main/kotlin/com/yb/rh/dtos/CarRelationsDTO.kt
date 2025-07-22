package com.yb.rh.dtos

import com.yb.rh.entities.Car
import javax.validation.constraints.NotNull

data class CarRelationsDTO(
    @NotNull
    var car: CarDTO,
    var isBlocking: List<CarDTO>,
    var isBlockedBy: List<CarDTO>
) {
    override fun toString(): String {
        return "CarRelationsDTO(car=${car.plateNumber}, isBlocking=${isBlocking.map { it.plateNumber }}, isBlockedBy=${isBlockedBy.map { it.plateNumber }})"
    }
}

data class CarRelations(
    var car: Car,
    var isBlocking: List<Car>,
    var isBlockedBy: List<Car>
) {
    override fun toString(): String {
        return "CarRelations(car=${car.plateNumber}, isBlocking=${isBlocking.map { it.plateNumber }}, isBlockedBy=${isBlockedBy.map { it.plateNumber }})"
    }
}

data class CarsRelationRequestDTO(
    @NotNull
    var blockingCarId: Long,
    @NotNull
    var blockedCarId: Long,
    @NotNull
    var userCarSituation: UserCarSituation
) {
    override fun toString(): String {
        return "CarsRelationsRequestDTO(blockingCarId=$blockingCarId, blockedCarId=$blockedCarId, userCarSituation=$userCarSituation)"
    }
}

enum class UserCarSituation {
    IS_BLOCKING,
    IS_BLOCKED,
}