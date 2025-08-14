package com.yb.rh

import com.yb.rh.dtos.*
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarsRelations
import com.yb.rh.entities.User
import com.yb.rh.entities.UserCar
import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.enum.NotificationsKind
import java.time.LocalDateTime

/**
 * Global test object builder for creating test data across all test classes
 */
object TestObjectBuilder {

    // User-related builders
    fun getUser(
        userId: Long = 0L,
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
        pushNotificationToken: String = "ExponentPushToken[test-token-123]",
        urlPhoto: String? = null,
        isActive: Boolean = true
    ) = User(
        firstName = firstName,
        lastName = lastName,
        email = email,
        pushNotificationToken = pushNotificationToken,
        urlPhoto = urlPhoto,
        isActive = isActive
    )

    fun getUserDTO(
        id: Long = 1L,
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
        urlPhoto: String? = null,
        pushNotificationToken: String? = "ExponentPushToken[test-token-123]"
    ) = UserDTO(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        urlPhoto = urlPhoto,
        pushNotificationToken = pushNotificationToken
    )

    fun getUserCreationDTO(
        firstName: String = "John",
        lastName: String = "Doe",
        email: String = "john.doe@example.com",
        pushNotificationToken: String = "ExponentPushToken[test-token-123]",
        urlPhoto: String? = null
    ) = UserCreationDTO(
        firstName = firstName,
        lastName = lastName,
        email = email,
        pushNotificationToken = pushNotificationToken,
        urlPhoto = urlPhoto
    )

    // Car-related builders
    fun getCar(
        id: Long = 0L,
        plateNumber: String = "ABC123",
        country: Countries = Countries.IL,
        brand: Brands = Brands.TESLA,
        model: String = "Model 3",
        color: Colors = Colors.WHITE,
        carLicenseExpireDate: LocalDateTime? = LocalDateTime.now().plusYears(1)
    ) = Car(
        plateNumber = plateNumber,
        country = country,
        brand = brand,
        model = model,
        color = color,
        carLicenseExpireDate = carLicenseExpireDate
    )

    fun getCarDTO(
        id: Long = 1L,
        plateNumber: String = "ABC123",
        country: Countries = Countries.IL,
        brand: Brands = Brands.TESLA,
        model: String = "Model 3",
        color: Colors = Colors.WHITE,
        carLicenseExpireDate: LocalDateTime? = LocalDateTime.now().plusYears(1),
        hasOwner: Boolean = false
    ) = CarDTO(
        id = id,
        plateNumber = plateNumber,
        country = country,
        brand = brand,
        model = model,
        color = color,
        carLicenseExpireDate = carLicenseExpireDate,
        hasOwner = hasOwner
    )

    fun getFindCarRequestDTO(
        plateNumber: String = "ABC123",
        country: Countries = Countries.IL,
        userId: Long? = 1L
    ) = FindCarRequestDTO(
        plateNumber = plateNumber,
        country = country,
        userId = userId
    )

    // UserCar-related builders
    fun getUserCar(
        id: Long = 0L,
        user: User = getUser(),
        car: Car = getCar()
    ) = UserCar(
        user = user,
        car = car
    )

    fun getUserCarDTO(
        user: UserDTO = getUserDTO(),
        car: CarDTO = getCarDTO()
    ) = UserCarDTO(
        user = user,
        car = car
    )

    fun getUserCarRequestDTO(
        userId: Long = 1L,
        carId: Long = 1L
    ) = UserCarRequestDTO(
        userId = userId,
        carId = carId
    )

    fun getUserCarsDTO(
        user: UserDTO = getUserDTO(),
        cars: List<CarDTO> = listOf(getCarDTO())
    ) = UserCarsDTO(
        user = user,
        cars = cars
    )

    fun getCarUsersDTO(
        car: CarDTO = getCarDTO(),
        users: List<UserDTO> = listOf(getUserDTO())
    ) = CarUsersDTO(
        car = car,
        users = users
    )

    // CarsRelations-related builders
    fun getCarsRelations(
        id: Long = 0L,
        blockingCar: Car = getCar(id = 0L, plateNumber = "BLOCKER"),
        blockedCar: Car = getCar(id = 0L, plateNumber = "BLOCKED")
    ) = CarsRelations(
        blockingCar = blockingCar,
        blockedCar = blockedCar
    )

    fun getCarRelations(
        car: Car = getCar(),
        isBlocking: List<Car> = listOf(getCar(id = 2L, plateNumber = "BLOCKED")),
        isBlockedBy: List<Car> = listOf(getCar(id = 3L, plateNumber = "BLOCKER"))
    ) = CarRelations(
        car = car,
        isBlocking = isBlocking,
        isBlockedBy = isBlockedBy
    )

    fun getCarRelationsDTO(
        car: CarDTO = getCarDTO(),
        isBlocking: List<CarDTO> = listOf(getCarDTO(id = 2L, plateNumber = "BLOCKED")),
        isBlockedBy: List<CarDTO> = listOf(getCarDTO(id = 3L, plateNumber = "BLOCKER")),
        message: String? = null
    ) = CarRelationsDTO(
        car = car,
        isBlocking = isBlocking,
        isBlockedBy = isBlockedBy,
        message = message
    )

    fun getCarsRelationRequestDTO(
        blockingCarId: Long = 1L,
        blockedCarId: Long = 2L,
        userCarSituation: UserCarSituation = UserCarSituation.IS_BLOCKING
    ) = CarsRelationRequestDTO(
        blockingCarId = blockingCarId,
        blockedCarId = blockedCarId,
        userCarSituation = userCarSituation
    )

    // Multiple objects builders for testing lists
    fun getMultipleUsers(count: Int = 3): List<User> = (1..count).map { 
        getUser(userId = it.toLong(), firstName = "User$it") 
    }

    fun getMultipleCars(count: Int = 3): List<Car> = (1..count).map { 
        getCar(id = it.toLong(), plateNumber = "CAR$it") 
    }

    fun getMultipleUserCars(count: Int = 3): List<UserCar> = (1..count).map { 
        getUserCar(id = it.toLong(), user = getUser(it.toLong()), car = getCar(it.toLong())) 
    }

    fun getMultipleCarsRelations(count: Int = 3): List<CarsRelations> = (1..count).map { 
        getCarsRelations(
            id = it.toLong(),
            blockingCar = getCar(id = it.toLong(), plateNumber = "BLOCKER$it"),
            blockedCar = getCar(id = (it + 100).toLong(), plateNumber = "BLOCKED$it")
        ) 
    }

    // Error scenarios builders
    fun getInvalidUserCreationDTO() = UserCreationDTO(
        firstName = "",
        lastName = "",
        email = "invalid-email",
        pushNotificationToken = ""
    )

    fun getInvalidCarRequestDTO() = FindCarRequestDTO(
        plateNumber = "",
        country = Countries.IL
    )

    fun getSelfBlockingRequestDTO() = CarsRelationRequestDTO(
        blockingCarId = 1L,
        blockedCarId = 1L,
        userCarSituation = UserCarSituation.IS_BLOCKING
    )

    // Notification-related builders
    fun getNotificationKinds(): List<NotificationsKind> = NotificationsKind.values().toList()

    // Edge cases
    fun getUserWithNullPhoto() = getUser(urlPhoto = null)
    fun getCarWithNullExpiry() = getCar(carLicenseExpireDate = null)
    fun getEmptyUserCarsDTO() = UserCarsDTO(getUserDTO(), emptyList())
    fun getEmptyCarUsersDTO() = CarUsersDTO(getCarDTO(), emptyList())
} 