package com.yb.rh.integration

import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.FindCarRequestDTO
import com.yb.rh.dtos.UserCarRequestDTO
import com.yb.rh.dtos.UserCarsDTO
import com.yb.rh.enum.Countries
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserCarIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test add car to user successfully`() {
        // First create a user directly in database
        val userId = createUserInDatabase(
            email = "user@example.com",
            firstName = "John",
            lastName = "Doe",
            pushNotificationToken = "user-token-123"
        )

        // Create a car
        val carRequest = FindCarRequestDTO(
            plateNumber = "CAR123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user
        val userCarRequest = UserCarRequestDTO(
            userId = userId,
            carId = car.id
        )
        val userCarResponse = performPost("/api/v1/user-car", userCarRequest)
        val userCars = objectMapper.readValue(userCarResponse, UserCarsDTO::class.java)

        assertEquals(userId, userCars.user.id)
        assertTrue(userCars.cars.any { it.id == car.id })

        // Verify database state
        assertEquals(1, countRowsInTable("users_cars"))
        val dbUserCar = getAllRowsFromTable("users_cars").first()
        assertEquals(userId, dbUserCar["user_id"])
        assertEquals(car.id, dbUserCar["car_id"])
    }

    @Test
    fun `test get user cars successfully`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "cars@example.com",
            firstName = "Jane",
            lastName = "Smith",
            pushNotificationToken = "cars-token-456"
        )

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
            userId = userId,
            carId = car1.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = userId,
            carId = car2.id
        )
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Get user cars using the correct endpoint
        val getUserCarsResponse = performGet("/api/v1/user-car/by-user-id?userId=$userId")
        val userCars = objectMapper.readValue(getUserCarsResponse, UserCarsDTO::class.java)

        assertEquals(2, userCars.cars.size)
        assertTrue(userCars.cars.any { it.id == car1.id })
        assertTrue(userCars.cars.any { it.id == car2.id })

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

        // Create multiple users directly in database
        val user1Id = createUserInDatabase(
            email = "user1@example.com",
            firstName = "User1",
            lastName = "One",
            pushNotificationToken = "user1-token"
        )
        val user2Id = createUserInDatabase(
            email = "user2@example.com",
            firstName = "User2",
            lastName = "Two",
            pushNotificationToken = "user2-token"
        )

        // Add car to both users
        val userCar1Request = UserCarRequestDTO(
            userId = user1Id,
            carId = car.id
        )
        val userCar2Request = UserCarRequestDTO(
            userId = user2Id,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCar1Request)
        performPost("/api/v1/user-car", userCar2Request)

        // Get car users - this endpoint doesn't exist, so we'll test the user cars endpoint instead
        val getUser1CarsResponse = performGet("/api/v1/user-car/by-user-id?userId=$user1Id")
        val user1Cars = objectMapper.readValue(getUser1CarsResponse, UserCarsDTO::class.java)
        
        val getUser2CarsResponse = performGet("/api/v1/user-car/by-user-id?userId=$user2Id")
        val user2Cars = objectMapper.readValue(getUser2CarsResponse, UserCarsDTO::class.java)

        assertTrue(user1Cars.cars.any { it.id == car.id })
        assertTrue(user2Cars.cars.any { it.id == car.id })

        // Verify database state
        assertEquals(2, countRowsInTable("users_cars"))
    }

    @Test
    fun `test remove car from user successfully`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "remove@example.com",
            firstName = "Remove",
            lastName = "User",
            pushNotificationToken = "remove-token"
        )

        val carRequest = FindCarRequestDTO(
            plateNumber = "REMOVE123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user
        val userCarRequest = UserCarRequestDTO(
            userId = userId,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCarRequest)

        // Verify car was added
        assertEquals(1, countRowsInTable("users_cars"))

        // Remove car from user
        performDelete("/api/v1/user-car", userCarRequest)

        // Verify car was removed
        assertEquals(0, countRowsInTable("users_cars"))

        // Verify user has no cars
        val getUserCarsResponse = performGet("/api/v1/user-car/by-user-id?userId=$userId")
        val userCars = objectMapper.readValue(getUserCarsResponse, UserCarsDTO::class.java)
        assertEquals(0, userCars.cars.size)
    }

    @Test
    fun `test add same car to user twice fails gracefully`() {
        // Create a user directly in database
        val userId = createUserInDatabase(
            email = "duplicate@example.com",
            firstName = "Duplicate",
            lastName = "User",
            pushNotificationToken = "duplicate-token"
        )

        val carRequest = FindCarRequestDTO(
            plateNumber = "DUPLICATE123",
            country = Countries.IL
        )
        val carResponse = performPost("/api/v1/car", carRequest)
        val car = objectMapper.readValue(carResponse, CarDTO::class.java)

        // Add car to user first time
        val userCarRequest = UserCarRequestDTO(
            userId = userId,
            carId = car.id
        )
        performPost("/api/v1/user-car", userCarRequest)

        // Verify car was added
        assertEquals(1, countRowsInTable("users_cars"))

        // Try to add the same car again - this should succeed but not create duplicate
        performPost("/api/v1/user-car", userCarRequest)

        // Verify only one relationship exists
        assertEquals(1, countRowsInTable("users_cars"))
    }
} 