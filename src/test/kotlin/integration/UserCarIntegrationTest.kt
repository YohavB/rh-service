package com.yb.rh.integration

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.FindCarRequestDTO
import com.yb.rh.dtos.UserCarRequestDTO
import com.yb.rh.dtos.UserCarDTO
import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.common.Countries
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.fasterxml.jackson.core.type.TypeReference

class UserCarIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test add car to user successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "user@example.com",
            firstName = "John",
            lastName = "Doe",
            pushNotificationToken = "user-token-123"
        )
        val userResponse = performPost("/api/v1/user", userCreationDTO)
        val user = objectMapper.readValue(userResponse, UserDTO::class.java)

        // Create a car
        val carRequest = FindCarRequestDTO(
            plateNumber = "CAR123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user
        val userCarRequest = UserCarRequestDTO(
            userId = user.id,
            carId = car.id
        )
        val userCarResponse = performPost("/api/v1/user-car", userCarRequest)
        val userCar = objectMapper.readValue(userCarResponse, UserCarDTO::class.java)

        assertEquals(user.id, userCar.user.id)
        assertEquals(car.id, userCar.car.id)

        // Verify database state
        assertEquals(1, countRowsInTable("users_cars"))
        val dbUserCar = getAllRowsFromTable("users_cars").first()
        assertEquals(user.id, dbUserCar["user_id"])
        assertEquals(car.id, dbUserCar["car_id"])
    }

    @Test
    fun `test get user cars successfully`() {
        // Create a user
        val userCreationDTO = UserCreationDTO(
            email = "cars@example.com",
            firstName = "Jane",
            lastName = "Smith",
            pushNotificationToken = "cars-token-456"
        )
        val userResponse = performPost("/api/v1/user", userCreationDTO)
        val user = objectMapper.readValue(userResponse, UserDTO::class.java)

        // Create multiple cars
        val car1Request = FindCarRequestDTO(
            plateNumber = "CAR001",
            country = Countries.IL
        )
        val car2Request = FindCarRequestDTO(
            plateNumber = "CAR002",
            country = Countries.IL
        )
        val car1Response = performPost("/api/v1/car", car1Request)
        val car2Response = performPost("/api/v1/car", car2Request)
        val car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        val car2 = objectMapper.readValue(car2Response, CarDTO::class.java)

        // Add cars to user
        val userCar1Request = UserCarRequestDTO(
            userId = user.id,
            carId = car1.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = user.id,
            carId = car2.id
        )
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Get user cars
        val getUserCarsResponse = performGet("/api/v1/user-car/user/${user.id}")
        val userCars = objectMapper.readValue(getUserCarsResponse, object : TypeReference<List<UserCarDTO>>() {})

        assertEquals(2, userCars.size)
        assertTrue(userCars.any { it.car.id == car1.id })
        assertTrue(userCars.any { it.car.id == car2.id })

        // Verify database state
        assertEquals(2, countRowsInTable("users_cars"))
    }

    @Test
    fun `test get car users successfully`() {
        // Create a car
        val carRequest = FindCarRequestDTO(
            plateNumber = "SHARED123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Create multiple users
        val user1CreationDTO = UserCreationDTO(
            email = "user1@example.com",
            firstName = "User1",
            lastName = "One",
            pushNotificationToken = "user1-token"
        )
        val user2CreationDTO = UserCreationDTO(
            email = "user2@example.com",
            firstName = "User2",
            lastName = "Two",
            pushNotificationToken = "user2-token"
        )
        val user1Response = performPost("/api/v1/user", user1CreationDTO)
        val user2Response = performPost("/api/v1/user", user2CreationDTO)
        val user1 = objectMapper.readValue(user1Response, UserDTO::class.java)
        val user2 = objectMapper.readValue(user2Response, UserDTO::class.java)

        // Add car to both users
        val userCar1Request = UserCarRequestDTO(
            userId = user1.id,
            carId = car.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = user2.id,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Get car users
        val getCarUsersResponse = performGet("/api/v1/user-car/car/${car.id}")
        val carUsers = objectMapper.readValue(getCarUsersResponse, object : TypeReference<List<UserCarDTO>>() {})

        assertEquals(2, carUsers.size)
        assertTrue(carUsers.any { it.user.id == user1.id })
        assertTrue(carUsers.any { it.user.id == user2.id })

        // Verify database state
        assertEquals(2, countRowsInTable("users_cars"))
    }

    @Test
    fun `test remove car from user successfully`() {
        // Create a user and car
        val userCreationDTO = UserCreationDTO(
            email = "remove@example.com",
            firstName = "Remove",
            lastName = "User",
            pushNotificationToken = "remove-token"
        )
        val userResponse = performPost("/api/v1/user", userCreationDTO)
        val user = objectMapper.readValue(userResponse, UserDTO::class.java)

        val carRequest = FindCarRequestDTO(
            plateNumber = "REMOVE123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user
        val userCarRequest = UserCarRequestDTO(
            userId = user.id,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCarRequest)

        // Verify car is added
        assertEquals(1, countRowsInTable("users_cars"))

        // Remove car from user
        performDelete("/api/v1/user-car/user/${user.id}/car/${car.id}")

        // Verify car is removed
        assertEquals(0, countRowsInTable("users_cars"))
    }

    @Test
    fun `test add same car to user twice fails gracefully`() {
        // Create a user and car
        val userCreationDTO = UserCreationDTO(
            email = "duplicate@example.com",
            firstName = "Duplicate",
            lastName = "User",
            pushNotificationToken = "duplicate-token"
        )
        val userResponse = performPost("/api/v1/user", userCreationDTO)
        val user = objectMapper.readValue(userResponse, UserDTO::class.java)

        val carRequest = FindCarRequestDTO(
            plateNumber = "DUPLICATE123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user first time
        val userCarRequest = UserCarRequestDTO(
            userId = user.id,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCarRequest)

        // Try to add same car again
        try {
            performPost("/api/v1/user-car", userCarRequest)
            // If no exception is thrown, that's also acceptable as the system might handle duplicates gracefully
        } catch (e: Exception) {
            // Expected to fail due to unique constraint
        }

        // Verify only one relationship exists
        assertEquals(1, countRowsInTable("users_cars"))
    }
} 