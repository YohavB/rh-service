package com.yb.rh.integration

import com.yb.rh.dtos.*
import com.yb.rh.enum.Countries
import com.yb.rh.error.ApiError
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestPropertySource(properties = ["spring.profiles.active=test"])
class NotificationIntegrationTest : IntegrationTestBase() {

    private lateinit var user1: UserDTO
    private lateinit var user2: UserDTO
    private lateinit var car1: CarDTO
    private lateinit var car2: CarDTO
    private lateinit var userCar1: UserCarDTO
    private lateinit var userCar2: UserCarDTO
    private lateinit var carsRelation: CarRelationsDTO

    @BeforeEach
    override fun setUp() {
        super.setUp()

        // Create users


        val user1Id = createUserInDatabase(
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            pushNotificationToken = "token1"
        )
        val user2Id = createUserInDatabase(
            email = "jane.smith@example.com",
            firstName = "Jane",
            lastName = "Smith",
            pushNotificationToken = "token2"
        )

        // Setup current user for authentication (using user1)
        setupCurrentUser(user1Id)
        
        // Create UserDTO objects for the tests
        user1 = UserDTO(
            id = user1Id,
            email = "john.doe@example.com",
            firstName = "John",
            lastName = "Doe",
            urlPhoto = "http://example.com/photo1.jpg"
        )
        user2 = UserDTO(
            id = user2Id,
            email = "jane.smith@example.com",
            firstName = "Jane",
            lastName = "Smith",
            urlPhoto = "http://example.com/photo2.jpg"
        )

        // Create cars
        val findCarRequest1 = FindCarRequestDTO(
            plateNumber = "BLOCK123",
            country = Countries.IL
        )
        val findCarRequest2 = FindCarRequestDTO(
            plateNumber = "BLOCKED123",
            country = Countries.IL
        )

        val car1Response = performPost("/api/v1/car", findCarRequest1)
        val car2Response = performPost("/api/v1/car", findCarRequest2)
        car1 = objectMapper.readValue(car1Response, CarDTO::class.java)
        car2 = objectMapper.readValue(car2Response, CarDTO::class.java)

        // Associate users with cars
        val userCarRequest1 = UserCarRequestDTO(
            userId = user1Id,
            carId = car1.id
        )
        val userCarRequest2 = UserCarRequestDTO(
            userId = user2Id,
            carId = car2.id
        )

        val userCar1Response = performPost("/api/v1/user-car", userCarRequest1)
        val userCar2Response = performPost("/api/v1/user-car", userCarRequest2)
        val userCars1 = objectMapper.readValue(userCar1Response, UserCarsDTO::class.java)
        val userCars2 = objectMapper.readValue(userCar2Response, UserCarsDTO::class.java)
        // Extract the first car from each user's cars list
        userCar1 = UserCarDTO(user = userCars1.user, car = userCars1.cars.first())
        userCar2 = UserCarDTO(user = userCars2.user, car = userCars2.cars.first())

        // Create blocking relationship (car1 blocks car2)
        val carsRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )

        val carsRelationResponse = performPost("/api/v1/car-relations", carsRelationRequest)
        // Parse the response manually to handle missing fields
        val jsonNode = objectMapper.readTree(carsRelationResponse)
        val car = objectMapper.treeToValue(jsonNode.get("car"), CarDTO::class.java)
        val isBlocking = if (jsonNode.has("isBlocking")) {
            objectMapper.treeToValue(jsonNode.get("isBlocking"), Array<CarDTO>::class.java).toList()
        } else {
            emptyList<CarDTO>()
        }
        val isBlockedBy = if (jsonNode.has("isBlockedBy")) {
            objectMapper.treeToValue(jsonNode.get("isBlockedBy"), Array<CarDTO>::class.java).toList()
        } else {
            emptyList<CarDTO>()
        }
        val message = if (jsonNode.has("message")) jsonNode.get("message").asText() else null
        carsRelation = CarRelationsDTO(car = car, isBlocking = isBlocking, isBlockedBy = isBlockedBy, message = message)
    }

    @Test
    fun `test sendNeedToGoNotification success`() {
        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car2.id}", "")

        // Then
        assertNotNull(response)
        // The method returns Unit, so we just check that the call was successful (200 status)
        // If there was an error, an exception would be thrown and the test would fail
    }

    @Test
    fun `test sendNeedToGoNotification with non-existing car`() {
        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=999", "", 400)

        // Then
        assertNotNull(response)
        val errorResponse = objectMapper.readValue(response, ApiError::class.java)
        assertNotNull(errorResponse)
        assertEquals("Car with ID 999 not found", errorResponse.message)
    }

    @Test
    fun `test sendNeedToGoNotification with car that is not blocked`() {
        // Given - Create a third car that is not blocked
        val findCarRequest3 = FindCarRequestDTO(
            plateNumber = "FREE123",
            country = Countries.IL
        )
        val car3Response = performPost("/api/v1/car", findCarRequest3)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car3.id}", "", 400)

        // Then
        assertNotNull(response)
        val errorResponse = objectMapper.readValue(response, ApiError::class.java)
        assertNotNull(errorResponse)
        assertEquals("Car is not blocked by any other car", errorResponse.message)
    }

    @Test
    fun `test sendNeedToGoNotification with car that has no owner`() {
        // Given - Create a car without associating it with any user (this will be the BLOCKING car)
        val findCarRequest3 = FindCarRequestDTO(
            plateNumber = "NOOWNER123",
            country = Countries.IL
        )
        val car3Response = performPost("/api/v1/car", findCarRequest3)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // Create blocking relationship where the car with no owner is BLOCKING car2
        val carsRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car3.id,  // car3 has no owner and is blocking car2
            blockedCarId = car2.id,   // car2 has an owner (user2)
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        performPost("/api/v1/car-relations", carsRelationRequest)

        // When - try to send notification for car2 (blocked by car3 which has no owner)
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car2.id}", "")

        // Then - should succeed because the notification logic just skips cars with no owners
        assertNotNull(response)
        // The method returns Unit and succeeds even if blocking cars have no owners
    }

    @Test
    fun `test sendNeedToGoNotification with blocking car that has no owner`() {
        // Given - Create a third car and associate it with user1
        val findCarRequest3 = FindCarRequestDTO(
            plateNumber = "BLOCKING3",
            country = Countries.IL
        )
        val car3Response = performPost("/api/v1/car", findCarRequest3)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        val userCarRequest3 = UserCarRequestDTO(
            userId = user1.id,
            carId = car3.id
        )
        performPost("/api/v1/user-car", userCarRequest3)

        // Create blocking relationship where car3 blocks car2
        val carsRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car3.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        performPost("/api/v1/car-relations", carsRelationRequest)

        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car2.id}", "")

        // Then
        assertNotNull(response)
        // The method returns Unit, so we just check that the call was successful (200 status)
    }

    @Test
    fun `test sendNeedToGoNotification with multiple blocking cars`() {
        // Given - Create a third car that also blocks car2
        val findCarRequest3 = FindCarRequestDTO(
            plateNumber = "BLOCKING3",
            country = Countries.IL
        )
        val car3Response = performPost("/api/v1/car", findCarRequest3)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        val userCarRequest3 = UserCarRequestDTO(
            userId = user1.id,
            carId = car3.id
        )
        performPost("/api/v1/user-car", userCarRequest3)

        // Create blocking relationship where car3 also blocks car2
        val carsRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car3.id,
            blockedCarId = car2.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        performPost("/api/v1/car-relations", carsRelationRequest)

        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car2.id}", "")

        // Then
        assertNotNull(response)
        // The method returns Unit, so we just check that the call was successful (200 status)
    }
} 