package com.yb.rh.integration

import com.yb.rh.dtos.*
import com.yb.rh.enum.Countries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CarRelationsIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test create car blocking relationship successfully`() {
        // Create a user and set up current user context
        val userId = createUserInDatabase("test@example.com", "Test", "User", "ExponentPushToken[test_token]")
        setupCurrentUser(userId)
        
        // Create two cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "BLOCK001",
            country = Countries.IL
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "BLOCK002",
            country = Countries.IL
        )
        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)
        
        // Associate cars with the current user
        val userCar1Request = UserCarRequestDTO(userId = userId, carId = car1.id)
        val userCar2Request = UserCarRequestDTO(userId = userId, carId = car2.id)
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Create blocking relationship
        val relationRequest = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val relationResponse = performPost("/api/v1/car-relations", relationRequest)
        // Parse the response as a list of CarRelationsDTO
        val relations = objectMapper.readValue(relationResponse, Array<CarRelationsDTO>::class.java).toList()
        
        // The response should contain at least one relation
        assertThat(relations).isNotEmpty()
        val relation = relations.first()

        assertEquals(car1.id, relation.car.id)
        // The API response only contains the car information, not the relationships
        // The relationships are created in the database, which we verify below

        // Verify database state
        assertEquals(1, countRowsInTable("cars_relations"))
        val dbRelation = getAllRowsFromTable("cars_relations").first()
        assertEquals(car1.id, dbRelation["blocking_car_id"])
        assertEquals(car2.id, dbRelation["blocked_car_id"])
    }







    @Test
    fun `test create duplicate blocking relationship fails gracefully`() {
        // Create a user and set up current user context
        val userId = createUserInDatabase("test2@example.com", "Test", "User", "ExponentPushToken[test_token2]")
        setupCurrentUser(userId)
        
        // Create two cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "DUPL001",
            country = Countries.IL
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "DUPL002",
            country = Countries.IL
        )
        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)
        
        // Associate cars with the current user
        val userCar1Request = UserCarRequestDTO(userId = userId, carId = car1.id)
        val userCar2Request = UserCarRequestDTO(userId = userId, carId = car2.id)
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Create first blocking relationship
        val relationRequest = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        performPost("/api/v1/car-relations", relationRequest)

        // Try to create duplicate relationship
        // The system should handle duplicates gracefully and return existing relationships
        val duplicateResponse = performPost("/api/v1/car-relations", relationRequest)
        val duplicateRelations = objectMapper.readValue(duplicateResponse, Array<CarRelationsDTO>::class.java).toList()
        assertThat(duplicateRelations).isNotEmpty()

        // Verify only one relationship exists
        assertEquals(1, countRowsInTable("cars_relations"))
    }

    @Test
    fun `test self-blocking prevention`() {
        // Create a user and set up current user context
        val userId = createUserInDatabase("test3@example.com", "Test", "User", "ExponentPushToken[test_token3]")
        setupCurrentUser(userId)
        
        // Create a car
        val carRequest = FindCarRequestDTO(
            plateNumber = "SELF001",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)
        
        // Associate car with the current user
        val userCarRequest = UserCarRequestDTO(userId = userId, carId = car.id)
        performPost("/api/v1/user-car", userCarRequest)

        // Try to create self-blocking relationship
        val relationRequest = CarsRelationRequestDTO(
            blockingCarId = car.id,
            blockedCarId = car.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )

        try {
            performPost("/api/v1/car-relations", relationRequest)
            // If no exception is thrown, that's also acceptable as the system might handle this gracefully
        } catch (e: Exception) {
            // Expected to fail due to self-blocking prevention
        }

        // Verify no relationship was created
        assertEquals(0, countRowsInTable("cars_relations"))
    }
} 