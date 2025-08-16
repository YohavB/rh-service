package com.yb.rh.integration

import com.yb.rh.dtos.*
import com.yb.rh.enum.Countries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class MainServiceIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test getUserCarRelations with user having multiple cars and relations`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            pushNotificationToken = "ExponentPushToken[test-token-123]"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Create multiple cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "CAR001",
            country = Countries.IL,
            userId = userId
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "CAR002",
            country = Countries.IL,
            userId = userId
        )
        val car3Request = FindCarRequestDTO(
            plateNumber = "CAR003",
            country = Countries.IL,
            userId = userId
        )

        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car3Response = performPost("/api/v1/car", car3Request)

        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // Create user-car relationships
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car1.id))
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car2.id))
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car3.id))

        // Create blocking relationships
        val relation1 = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val relation2 = CarsRelationRequestDTO(
            blockingCarId = car3.id,
            blockedCarId = car1.id,
            userCarSituation = UserCarSituation.IS_BLOCKED
        )

        performPost("/api/v1/car-relations", relation1)
        performPost("/api/v1/car-relations", relation2)

        // Get user car relations
        val relationsResponse = performGet("/api/v1/car-relations/by-user")
        val relations = objectMapper.readValue(relationsResponse, Array<CarRelationsDTO>::class.java).toList()

        // Verify the response
        assertNotNull(relations)
        assertEquals(3, relations.size) // Should have relations for all 3 cars

        // Find car1 relations (should be blocking car2 and blocked by car3)
        val car1Relations = relations.find { it.car.id == car1.id }
        assertNotNull(car1Relations)
        assertEquals(1, car1Relations!!.isBlocking.size) // Blocking car2
        assertEquals(1, car1Relations.isBlockedBy.size) // Blocked by car3
        assertEquals(car2.id, car1Relations.isBlocking[0].id)
        assertEquals(car3.id, car1Relations.isBlockedBy[0].id)

        // Find car2 relations (should only be blocked by car1)
        val car2Relations = relations.find { it.car.id == car2.id }
        assertNotNull(car2Relations)
        assertEquals(0, car2Relations!!.isBlocking.size)
        assertEquals(1, car2Relations.isBlockedBy.size) // Blocked by car1
        assertEquals(car1.id, car2Relations.isBlockedBy[0].id)

        // Find car3 relations (should only be blocking car1)
        val car3Relations = relations.find { it.car.id == car3.id }
        assertNotNull(car3Relations)
        assertEquals(1, car3Relations!!.isBlocking.size) // Blocking car1
        assertEquals(0, car3Relations.isBlockedBy.size)
        assertEquals(car1.id, car3Relations.isBlocking[0].id)
    }

    @Test
    fun `test getUserCarRelations with user having single car and no relations`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "jane.smith@example.com",
            firstName = "Jane",
            lastName = "Smith",
            pushNotificationToken = "ExponentPushToken[test-token-456]"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Create a single car
        val carRequest = FindCarRequestDTO(
            plateNumber = "SINGLE",
            country = Countries.IL,
            userId = userId
        )

        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Create user-car relationship
        val userCarRequest = UserCarRequestDTO(userId = userId, carId = car.id)
        performPost("/api/v1/user-car", userCarRequest)

        // Get user car relations
        val relationsResponse = performGet("/api/v1/car-relations/by-user")
        val relations = objectMapper.readValue(relationsResponse, Array<CarRelationsDTO>::class.java).toList()

        // Verify the response
        assertNotNull(relations)
        assertEquals(1, relations.size) // Should have relations for 1 car
        assertEquals(car.id, relations[0].car.id)
        assertEquals(0, relations[0].isBlocking.size)
        assertEquals(0, relations[0].isBlockedBy.size)
    }

    @Test
    fun `test getUserCarRelations with user having no cars`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "bob.johnson@example.com",
            firstName = "Bob",
            lastName = "Johnson",
            pushNotificationToken = "ExponentPushToken[test-token-789]"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Don't create any cars for this user

        // Get user car relations
        val relationsResponse = performGet("/api/v1/car-relations/by-user")
        val relations = objectMapper.readValue(relationsResponse, Array<CarRelationsDTO>::class.java).toList()

        // Verify the response
        assertNotNull(relations)
        assertEquals(0, relations.size) // Should have no relations
    }

    @Test
    fun `test getUserCarRelations with complex blocking chain`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "alice.brown@example.com",
            firstName = "Alice",
            lastName = "Brown",
            pushNotificationToken = "ExponentPushToken[test-token-abc]"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Create multiple cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "CHAIN1",
            country = Countries.IL,
            userId = userId
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "CHAIN2",
            country = Countries.IL,
            userId = userId
        )
        val car3Request = FindCarRequestDTO(
            plateNumber = "CHAIN3",
            country = Countries.IL,
            userId = userId
        )

        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car3Response = performPost("/api/v1/car", car3Request)

        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // Create user-car relationships
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car1.id))
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car2.id))
        performPost("/api/v1/user-car", UserCarRequestDTO(userId = userId, carId = car3.id))

        // Create a chain: car1 blocks car2, car2 blocks car3
        val relation1 = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val relation2 = CarsRelationRequestDTO(
            blockingCarId = car2.id,
            blockedCarId = car3.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )

        performPost("/api/v1/car-relations", relation1)
        performPost("/api/v1/car-relations", relation2)

        // Get user car relations
        val relationsResponse = performGet("/api/v1/car-relations/by-user")
        val relations = objectMapper.readValue(relationsResponse, Array<CarRelationsDTO>::class.java).toList()

        // Verify the response
        assertNotNull(relations)
        assertEquals(3, relations.size) // Should have relations for all 3 cars

        // Find car1 relations (should only be blocking car2)
        val car1Relations = relations.find { it.car.id == car1.id }
        assertNotNull(car1Relations)
        assertEquals(1, car1Relations!!.isBlocking.size) // Blocking car2
        assertEquals(0, car1Relations.isBlockedBy.size) // Not blocked by anyone
        assertEquals(car2.id, car1Relations.isBlocking[0].id)

        // Find car2 relations (should be blocking car3 and blocked by car1)
        val car2Relations = relations.find { it.car.id == car2.id }
        assertNotNull(car2Relations)
        assertEquals(1, car2Relations!!.isBlocking.size) // Blocking car3
        assertEquals(1, car2Relations.isBlockedBy.size) // Blocked by car1
        assertEquals(car3.id, car2Relations.isBlocking[0].id)
        assertEquals(car1.id, car2Relations.isBlockedBy[0].id)

        // Find car3 relations (should only be blocked by car2)
        val car3Relations = relations.find { it.car.id == car3.id }
        assertNotNull(car3Relations)
        assertEquals(0, car3Relations!!.isBlocking.size)
        assertEquals(1, car3Relations.isBlockedBy.size) // Blocked by car2
        assertEquals(car2.id, car3Relations.isBlockedBy[0].id)
    }
} 