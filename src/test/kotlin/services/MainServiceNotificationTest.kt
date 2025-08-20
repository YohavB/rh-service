package com.yb.rh.services

import com.yb.rh.dtos.CarUsersDTO
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.enum.NotificationsKind
import com.yb.rh.error.RHException
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
        
        val currentUserService = mockk<CurrentUserService>()
        mainService = MainService(
            userService,
            carService,
            userCarService,
            carsRelationsService,
            notificationService,
            currentUserService
        )
    }

    @Test
    fun `sendNeedToGoNotification should throw exception when car is not blocked by any other car`() {
        // Given
        val carId = 1L
        val car = createTestCar(carId, "ABC123")
        
        every { carService.getCarById(carId) } returns car
        every { carsRelationsService.findCarRelationsByCar(car) } returns createTestCarRelations(car, emptyList(), emptyList())

        // When & Then
        val exception = assertThrows<RHException> {
            mainService.sendNeedToGoNotification(carId)
        }
        
        assert(exception.message?.contains("Car is not blocked by any other car") == true)
    }

    @Test
    fun `sendBlockedNotification should work when car has no users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val emptyCarUsers = CarUsersDTO(car.toDto(false), emptyList())
        
        every { userCarService.getCarUsersByCar(car) } returns emptyCarUsers

        // When
            mainService.sendBlockedNotification(car)
        
        // Then - should not throw exception, just do nothing
        verify(exactly = 0) { notificationService.sendPushNotification(any(), any()) }
    }

    @Test
    fun `sendBlockingNotification should work when car has no users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val emptyCarUsers = CarUsersDTO(car.toDto(false), emptyList())
        
        every { userCarService.getCarUsersByCar(car) } returns emptyCarUsers

        // When
            mainService.sendBlockingNotification(car)
        
        // Then - should not throw exception, just do nothing
        verify(exactly = 0) { notificationService.sendPushNotification(any(), any()) }
    }

    @Test
    fun `sendNeedToGoNotification should work when car has users`() {
        // Given
        val carId = 1L
        val car = createTestCar(carId, "ABC123")
        val user = createTestUser(1L, "john@example.com")
        val carUsers = CarUsersDTO(car.toDto(false), listOf(user.toDto()))
        val blockingCar = createTestCar(2L, "XYZ789")
        
        every { carService.getCarById(carId) } returns car
        every { carsRelationsService.findCarRelationsByCar(car) } returns createTestCarRelations(car, emptyList(), listOf(blockingCar))
        every { carsRelationsService.findCarRelationsByCar(blockingCar) } returns createTestCarRelations(blockingCar, emptyList(), emptyList())
        every { userCarService.getCarUsersByCar(car) } returns carUsers
        every { userCarService.getCarUsersByCar(blockingCar) } returns CarUsersDTO(blockingCar.toDto(false), listOf(user.toDto()))
        every { userService.getUserById(1L) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        mainService.sendNeedToGoNotification(carId)

        // Then
        // Notification sent successfully
        verify { notificationService.sendPushNotification(user, NotificationsKind.NEED_TO_GO) }
    }

    @Test
    fun `sendBlockedNotification should work when car has users`() {
        // Given
        val car = createTestCar(1L, "ABC123")
        val user = createTestUser(1L, "john@example.com")
        val carUsers = CarUsersDTO(car.toDto(false), listOf(user.toDto()))
        
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
        val carUsers = CarUsersDTO(car.toDto(false), listOf(user.toDto()))
        
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
            manufacturingYear = 2020,
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