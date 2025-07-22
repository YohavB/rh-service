package com.yb.rh.integration

import com.fasterxml.jackson.core.type.TypeReference
import com.yb.rh.common.Countries
import com.yb.rh.dtos.*
import com.yb.rh.utils.SuccessResponse
import com.yb.rh.utils.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        val userCreation1 = UserCreationDTO(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "token1",
            urlPhoto = "http://example.com/photo1.jpg"
        )
        val userCreation2 = UserCreationDTO(
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            pushNotificationToken = "token2",
            urlPhoto = "http://example.com/photo2.jpg"
        )

        val user1Response = performPost("/api/v1/user", userCreation1)
        val user2Response = performPost("/api/v1/user", userCreation2)
        user1 = objectMapper.readValue(user1Response, UserDTO::class.java)
        user2 = objectMapper.readValue(user2Response, UserDTO::class.java)

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
            userId = user1.id,
            carId = car1.id
        )
        val userCarRequest2 = UserCarRequestDTO(
            userId = user2.id,
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
        carsRelation = CarRelationsDTO(car = car, isBlocking = isBlocking, isBlockedBy = isBlockedBy)
    }

    @Test
    fun `test sendNeedToGoNotification success`() {
        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car2.id}", "")

        // Then
        assertNotNull(response)
        assertTrue(response.contains("Notification sent successfully"))
    }

    @Test
    fun `test sendNeedToGoNotification with non-existing car`() {
        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=999", "", 400)

        // Then
        assertNotNull(response)
        val errorResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals("Car with ID 999 not found", errorResponse.cause)
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
        val errorResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals("Car is not blocked by any other car", errorResponse.cause)
    }

    @Test
    fun `test sendNeedToGoNotification with car that has no owner`() {
        // Given - Create a car without associating it with any user
        val findCarRequest3 = FindCarRequestDTO(
            plateNumber = "NOOWNER123",
            country = Countries.IL
        )
        val car3Response = performPost("/api/v1/car", findCarRequest3)
        val car3 = objectMapper.readValue(car3Response, CarDTO::class.java)

        // Create blocking relationship with this car
        val carsRelationRequest = CarsRelationRequestDTO(
            blockingCarId = car1.id,
            blockedCarId = car3.id,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        performPost("/api/v1/car-relations", carsRelationRequest)

        // When
        val response = performPost("/api/v1/notification/send-need-to-go?blockedCarId=${car3.id}", "", 400)

        // Then
        assertNotNull(response)
        val errorResponse = objectMapper.readValue(response, ErrorResponse::class.java)
        assertNotNull(errorResponse)
        assertEquals("Car has no owner", errorResponse.cause)
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
        assertTrue(response.contains("Notification sent successfully"))
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
        assertTrue(response.contains("Notification sent successfully"))
    }
} 