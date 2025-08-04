package com.yb.rh.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.yb.rh.entities.Car
import jakarta.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.ALWAYS)
data class CarRelationsDTO(
    @NotNull
    @JsonProperty("car")
    var car: CarDTO,
    @JsonProperty("isBlocking")
    var isBlocking: List<CarDTO>,
    @JsonProperty("isBlockedBy")
    var isBlockedBy: List<CarDTO>,
    @JsonProperty("message")
    var message: String? = null
) {
    override fun toString(): String {
        return "CarRelationsDTO(car=${car.plateNumber}, isBlocking=${isBlocking.map { it.plateNumber }}, isBlockedBy=${isBlockedBy.map { it.plateNumber }}, message=$message)"
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
    IS_BLOCKED;

    @com.fasterxml.jackson.annotation.JsonValue
    fun getValue(): String = name
}