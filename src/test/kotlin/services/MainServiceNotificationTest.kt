package com.yb.rh.services

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.common.NotificationsKind
import com.yb.rh.dtos.CarUsersDTO
import com.yb.rh.dtos.UserDTO
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.UserRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class MainServiceNotificationTest {

    private lateinit var mainService: MainService
    private lateinit var userService: UserService
    private lateinit var carService: CarService
    private lateinit var userCarService: UserCarService
    private lateinit var carsRelationsService: CarsRelationsService
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setUp() {
        userService = mockk()
        carService = mockk()
        userCarService = mockk()
        carsRelationsService = mockk()
        notificationService = mockk()
        
        mainService = MainService(
            userService,
            carService,
            userCarService,
            carsRelationsService,
            notificationService
        )
    }

    @Test
    fun `sendNeedToGoNotification should throw CAR_HAS_NO_OWNER error when car has no users`() {
        // Given
        val carId = 1L
        val car = createTestCar(carId, "ABC123")
        val blockingCar = createTestCar(2L, "XYZ789")
        val emptyCarUsers = CarUsersDTO(car.toDto(), emptyList())
        
        every { carService.getCarById(carId) } returns car
        every { carsRelationsService.findCarRelations(car) } returns createTestCarRelations(car, emptyList(), listOf(blockingCar))
        every { userCarService.getCarUsersByCar(car) } returns emptyCarUsers

        // When & Then
        val exception = assertThrows<RHException> {
            mainService.sendNeedToGoNotification(carId)
        }
        
        assert(exception.errorType == ErrorType.CAR_HAS_NO_OWNER)
        assert(exception.message?.contains("This car has no user so no one would be notified") == true)
    }

    @Test
    fun `sendBlockedNotification should throw CAR_HAS_NO_OWNER error when car has no users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val emptyCarUsers = CarUsersDTO(car.toDto(), emptyList())
        
        every { userCarService.getCarUsersByCar(car) } returns emptyCarUsers

        // When & Then
        val exception = assertThrows<RHException> {
            mainService.sendBlockedNotification(car)
        }
        
        assert(exception.errorType == ErrorType.CAR_HAS_NO_OWNER)
        assert(exception.message?.contains("This car has no user so no one would be notified") == true)
    }

    @Test
    fun `sendBlockingNotification should throw CAR_HAS_NO_OWNER error when car has no users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val emptyCarUsers = CarUsersDTO(car.toDto(), emptyList())
        
        every { userCarService.getCarUsersByCar(car) } returns emptyCarUsers

        // When & Then
        val exception = assertThrows<RHException> {
            mainService.sendBlockingNotification(car)
        }
        
        assert(exception.errorType == ErrorType.CAR_HAS_NO_OWNER)
        assert(exception.message?.contains("This car has no user so no one would be notified") == true)
    }

    @Test
    fun `sendNeedToGoNotification should work when car has users`() {
        // Given
        val carId = 1L
        val car = createTestCar(carId, "ABC123")
        val user = createTestUser(1L, "john@example.com")
        val carUsers = CarUsersDTO(car.toDto(), listOf(user.toDto()))
        val blockingCar = createTestCar(2L, "XYZ789")
        
        every { carService.getCarById(carId) } returns car
        every { carsRelationsService.findCarRelations(car) } returns createTestCarRelations(car, emptyList(), listOf(blockingCar))
        every { carsRelationsService.findCarRelations(blockingCar) } returns createTestCarRelations(blockingCar, emptyList(), emptyList())
        every { userCarService.getCarUsersByCar(car) } returns carUsers
        every { userCarService.getCarUsersByCar(blockingCar) } returns CarUsersDTO(blockingCar.toDto(), listOf(user.toDto()))
        every { userService.getUserById(1L) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        val result = mainService.sendNeedToGoNotification(carId)

        // Then
        assert(result == "Notification sent successfully")
        verify { notificationService.sendPushNotification(user, NotificationsKind.NEED_TO_GO) }
    }

    @Test
    fun `sendBlockedNotification should work when car has users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val user = createTestUser(1L, "john@example.com")
        val carUsers = CarUsersDTO(car.toDto(), listOf(user.toDto()))
        
        every { userCarService.getCarUsersByCar(car) } returns carUsers
        every { userService.getUserById(1L) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        mainService.sendBlockedNotification(car)

        // Then
        verify { notificationService.sendPushNotification(user, NotificationsKind.BEEN_BLOCKED) }
    }

    @Test
    fun `sendBlockingNotification should work when car has users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val user = createTestUser(1L, "john@example.com")
        val carUsers = CarUsersDTO(car.toDto(), listOf(user.toDto()))
        
        every { userCarService.getCarUsersByCar(car) } returns carUsers
        every { userService.getUserById(1L) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        mainService.sendBlockingNotification(car)

        // Then
        verify { notificationService.sendPushNotification(user, NotificationsKind.BEEN_BLOCKING) }
    }

    private fun createTestCar(id: Long, plateNumber: String): Car {
        return Car(
            plateNumber = plateNumber,
            country = Countries.IL,
            brand = Brands.TOYOTA,
            model = "Corolla",
            color = Colors.WHITE,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            creationTime = LocalDateTime.now(),
            updateTime = LocalDateTime.now(),
            id = id
        )
    }

    private fun createTestUser(id: Long, email: String): User {
        return User(
            firstName = "John",
            lastName = "Doe",
            email = email,
            pushNotificationToken = "ExponentPushToken[test_token]",
            urlPhoto = "https://example.com/photo.jpg",
            isActive = true,
            creationTime = LocalDateTime.now(),
            updateTime = LocalDateTime.now(),
            userId = id
        )
    }

    private fun createTestCarRelations(car: Car, isBlocking: List<Car>, isBlockedBy: List<Car>): com.yb.rh.dtos.CarRelations {
        return com.yb.rh.dtos.CarRelations(
            car = car,
            isBlocking = isBlocking,
            isBlockedBy = isBlockedBy
        )
    }
} 