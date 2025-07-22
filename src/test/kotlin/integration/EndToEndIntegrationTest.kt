package com.yb.rh.integration

import com.fasterxml.jackson.core.type.TypeReference
import com.yb.rh.common.Countries
import com.yb.rh.dtos.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class EndToEndIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test complete user car blocking scenario`() {
        // Create users
        val user1CreationDTO = UserCreationDTO(
            email = "driver1@example.com",
            firstName = "Driver1",
            lastName = "One",
            pushNotificationToken = "driver1-token"
        )
        val user2CreationDTO = UserCreationDTO(
            email = "driver2@example.com",
            firstName = "Driver2",
            lastName = "Two",
            pushNotificationToken = "driver2-token"
        )
        val user3CreationDTO = UserCreationDTO(
            email = "driver3@example.com",
            firstName = "Driver3",
            lastName = "Three",
            pushNotificationToken = "driver3-token"
        )

        val user1Response = performPost("/api/v1/user", user1CreationDTO)
        val user2Response = performPost("/api/v1/user", user2CreationDTO)
        val user3Response = performPost("/api/v1/user", user3CreationDTO)
        val user1 = objectMapper.readValue(user1Response, UserDTO::class.java)
        val user2 = objectMapper.readValue(user2Response, UserDTO::class.java)
        val user3 = objectMapper.readValue(user3Response, UserDTO::class.java)

        // Create cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "E2E001",
            country = Countries.IL
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "E2E002",
            country = Countries.IL
        )
        val car3Request = FindCarRequestDTO(
            plateNumber = "E2E003",
            country = Countries.IL
        )

        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car3Response = performPost("/api/v1/car", car3Request)
        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // Associate users with cars
        val userCar1Request = UserCarRequestDTO(
            userId = user1.id,
            carId = car1.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = user2.id,
            carId = car2.id
        )
        val userCar3Request = UserCarRequestDTO(
            userId = user3.id,
            carId = car3.id
        )

        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)
        performPost("/api/v1/user-car", userCar3Request)

        // Create blocking relationships: car1 blocks car2, car2 blocks car3
        val relation1Request = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val relation2Request = CarsRelationRequestDTO(
            blockingCarId = car2.id,
            blockedCarId = car3.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )

        performPost("/api/v1/car-relations", relation1Request)
        performPost("/api/v1/car-relations", relation2Request)

        // Verify complete scenario
        assertEquals(3, countRowsInTable("users"))
        assertEquals(3, countRowsInTable("cars"))
        assertEquals(3, countRowsInTable("users_cars"))
        assertEquals(2, countRowsInTable("cars_relations"))

        // Test getting user's cars
        val user1CarsResponse = performGet("/api/v1/user-car/user/${user1.id}")
        val user1Cars = objectMapper.readValue(user1CarsResponse, object : TypeReference<List<UserCarDTO>>() {})
        assertEquals(1, user1Cars.size)
        assertEquals(car1.id, user1Cars.first().car.id)

        // Test getting car's users
        val car1UsersResponse = performGet("/api/v1/user-car/car/${car1.id}")
        val car1Users = objectMapper.readValue(car1UsersResponse, object : TypeReference<List<UserCarDTO>>() {})
        assertEquals(1, car1Users.size)
        assertEquals(user1.id, car1Users.first().user.id)

        // Test getting blocking relationships
        val car1BlockingResponse = performGet("/api/v1/car-relations/blocking/${car1.id}")
        val car1Blocking = objectMapper.readValue(car1BlockingResponse, object : TypeReference<List<CarRelationsDTO>>() {})
        assertEquals(1, car1Blocking.size)
        assertEquals(car2.id, car1Blocking.first().isBlocking.first().id)

        // Test getting blocked cars
        val car2BlockedResponse = performGet("/api/v1/car-relations/blocked/${car2.id}")
        val car2Blocked = objectMapper.readValue(car2BlockedResponse, object : TypeReference<List<CarRelationsDTO>>() {})
        assertEquals(1, car2Blocked.size)
        assertEquals(car1.id, car2Blocked.first().isBlockedBy.first().id)
    }

    @Test
    fun `test user deactivation and reactivation flow`() {
        // Create user and car
        val userCreationDTO = UserCreationDTO(
            email = "deactivate@example.com",
            firstName = "Deactivate",
            lastName = "User",
            pushNotificationToken = "deactivate-token"
        )
        val userResponse = performPost("/api/v1/user", userCreationDTO)
        val user = objectMapper.readValue(userResponse, UserDTO::class.java)

        val carRequest = FindCarRequestDTO(
            plateNumber = "DEACTIVATE123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Associate user with car
        val userCarRequest = UserCarRequestDTO(
            userId = user.id,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCarRequest)

        // Verify initial state
        assertEquals(1, countRowsInTable("users"))
        assertEquals(1, countRowsInTable("cars"))
        assertEquals(1, countRowsInTable("users_cars"))

        // Deactivate user
        performPost("/api/v1/user/deactivate/${user.id}", "")

        // Verify user is deactivated in database
        val dbUser = getRowFromTable("users", user.id)
        assertNotNull(dbUser)
        assertEquals(false, dbUser!!["is_active"])

        // Reactivate user
        performPost("/api/v1/user/activate/${user.id}", "")

        // Verify user is reactivated in database
        val reactivatedDbUser = getRowFromTable("users", user.id)
        assertNotNull(reactivatedDbUser)
        assertEquals(true, reactivatedDbUser!!["is_active"])

        // Verify relationships still exist
        assertEquals(1, countRowsInTable("users_cars"))
    }

    @Test
    fun `test car deletion and relationship cleanup`() {
        // Create users and cars
        val user1CreationDTO = UserCreationDTO(
            email = "cleanup1@example.com",
            firstName = "Cleanup1",
            lastName = "One",
            pushNotificationToken = "cleanup1-token"
        )
        val user2CreationDTO = UserCreationDTO(
            email = "cleanup2@example.com",
            firstName = "Cleanup2",
            lastName = "Two",
            pushNotificationToken = "cleanup2-token"
        )

        val user1Response = performPost("/api/v1/user", user1CreationDTO)
        val user2Response = performPost("/api/v1/user", user2CreationDTO)
        val user1 = objectMapper.readValue(user1Response, UserDTO::class.java)
        val user2 = objectMapper.readValue(user2Response, UserDTO::class.java)

        val car1Request = FindCarRequestDTO(
            plateNumber = "CLEANUP001",
            country = Countries.IL
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "CLEANUP002",
            country = Countries.IL
        )

        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)

        // Create relationships
        val userCar1Request = UserCarRequestDTO(
            userId = user1.id,
            carId = car1.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = user2.id,
            carId = car2.id
        )
        val blockingRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )

        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)
        performPost("/api/v1/car-relations", blockingRelationRequest)

        // Verify initial state
        assertEquals(2, countRowsInTable("users"))
        assertEquals(2, countRowsInTable("cars"))
        assertEquals(2, countRowsInTable("users_cars"))
        assertEquals(1, countRowsInTable("cars_relations"))

        // Delete car1
        performDelete("/api/v1/car/${car1.id}")

        // Verify car1 is deleted and relationships are cleaned up
        assertEquals(1, countRowsInTable("cars"))
        assertEquals(1, countRowsInTable("users_cars"))
        assertEquals(0, countRowsInTable("cars_relations"))

        // Verify car2 still exists
        val remainingCarResponse = performGet("/api/v1/car/${car2.id}")
        val remainingCar = objectMapper.readValue(remainingCarResponse, CarDTO::class.java)
        assertEquals("CLEANUP002", remainingCar.plateNumber)
    }

    @Test
    fun `test complex blocking chain scenario`() {
        // Create 4 cars in a chain: car1 -> car2 -> car3 -> car4
        val cars = mutableListOf<CarDTO>()
        for (i in 1..4) {
            val carRequest = FindCarRequestDTO(
                plateNumber = "CHAIN00$i",
                country = Countries.IL
            )
            val carResponse = performPost("/api/v1/car", carRequest)
            val car = objectMapper.readValue(carResponse, CarDTO::class.java)
            cars.add(car)
        }

        // Create blocking chain
        for (i in 0..2) {
            val relationRequest = CarsRelationRequestDTO(
                blockingCarId = cars[i].id,
                blockedCarId = cars[i + 1].id,
                userCarSituation = UserCarSituation.IS_BLOCKING
            )
            performPost("/api/v1/car-relations", relationRequest)
        }

        // Verify chain
        assertEquals(4, countRowsInTable("cars"))
        assertEquals(3, countRowsInTable("cars_relations"))

        // Test getting all cars blocked by car1 (should be car2, car3, car4)
        val car1BlockingResponse = performGet("/api/v1/car-relations/blocking/${cars[0].id}")
        val car1Blocking = objectMapper.readValue(car1BlockingResponse, object : TypeReference<List<CarRelationsDTO>>() {})
        assertEquals(1, car1Blocking.size) // Direct blocking only
        assertEquals(cars[1].id, car1Blocking.first().isBlocking.first().id)

        // Test getting all cars that block car4 (should be car3, car2, car1)
        val car4BlockedResponse = performGet("/api/v1/car-relations/blocked/${cars[3].id}")
        val car4Blocked = objectMapper.readValue(car4BlockedResponse, object : TypeReference<List<CarRelationsDTO>>() {})
        assertEquals(1, car4Blocked.size) // Direct blocking only
        assertEquals(cars[2].id, car4Blocked.first().isBlockedBy.first().id)

        // Remove middle car (car2) and verify chain is broken
        performDelete("/api/v1/car-relations/${cars[1].id}/${cars[2].id}")

        // Verify chain is broken
        assertEquals(2, countRowsInTable("cars_relations"))

        // Test that car1 no longer blocks car3
        val car1BlockingAfterResponse = performGet("/api/v1/car-relations/blocking/${cars[0].id}")
        val car1BlockingAfter = objectMapper.readValue(car1BlockingAfterResponse, object : TypeReference<List<CarRelationsDTO>>() {})
        assertEquals(1, car1BlockingAfter.size)
        assertEquals(cars[1].id, car1BlockingAfter.first().isBlocking.first().id)
    }
} 