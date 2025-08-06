package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.dtos.UserCarSituation
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MainServiceTest {
    private lateinit var carService: CarService
    private lateinit var userService: UserService
    private lateinit var userCarService: UserCarService
    private lateinit var carsRelationsService: CarsRelationsService
    private lateinit var notificationService: NotificationService
    private lateinit var currentUserService: CurrentUserService
    private lateinit var mainService: MainService

    @BeforeEach
    fun setUp() {
        carService = mockk()
        userService = mockk()
        userCarService = mockk()
        carsRelationsService = mockk()
        notificationService = mockk()
        currentUserService = mockk()
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
    fun `test createCarsRelations success IS_BLOCKING`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 1L,
            blockedCarId = 2L,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        val carUsersDTO = TestObjectBuilder.getCarUsersDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        
        every { blockingCar.id } returns 1L
        every { blockedCar.id } returns 2L
        every { blockingCar.plateNumber } returns "BLOCKER"
        every { blockedCar.plateNumber } returns "BLOCKED"
        every { carService.getCarById(1L) } returns blockingCar
        every { carService.getCarById(2L) } returns blockedCar
        every { carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar) } returns false
        every { carsRelationsService.createCarsRelation(blockingCar, blockedCar) } just Runs
        every { carsRelationsService.findCarRelationsByCar(any(), any()) } returns carRelationsDTO
        every { userCarService.getCarUsersByCar(any()) } returns carUsersDTO
        every { userService.getUserById(any()) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        val result = mainService.createCarsRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { 
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar)
            carsRelationsService.createCarsRelation(blockingCar, blockedCar)
            carsRelationsService.findCarRelationsByCar(any(), any())
        }
    }

    @Test
    fun `test createCarsRelations success IS_BLOCKED`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 1L,
            blockedCarId = 2L,
            userCarSituation = UserCarSituation.IS_BLOCKED
        )
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        val carUsersDTO = TestObjectBuilder.getCarUsersDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        
        every { blockingCar.id } returns 1L
        every { blockedCar.id } returns 2L
        every { blockingCar.plateNumber } returns "BLOCKER"
        every { blockedCar.plateNumber } returns "BLOCKED"
        every { carService.getCarById(1L) } returns blockingCar
        every { carService.getCarById(2L) } returns blockedCar
        every { carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar) } returns false
        every { carsRelationsService.createCarsRelation(blockingCar, blockedCar) } just Runs
        every { carsRelationsService.findCarRelationsByCar(any(), any()) } returns carRelationsDTO
        every { userCarService.getCarUsersByCar(any()) } returns carUsersDTO
        every { userService.getUserById(any()) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        val result = mainService.createCarsRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { 
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar)
            carsRelationsService.createCarsRelation(blockingCar, blockedCar)
            carsRelationsService.findCarRelationsByCar(any(), any())
        }
    }

    @Test
    fun `test createCarsRelations self blocking`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 1L,
            blockedCarId = 1L,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val car = TestObjectBuilder.getCar(id = 1L)
        
        every { carService.getCarById(1L) } returns car

        // When & Then
        assertThrows<IllegalArgumentException> {
            mainService.createCarsRelations(carsRelationRequestDTO)
        }
        verify { 
            carService.getCarById(1L)
            carService.getCarById(1L)
        }
        verify(exactly = 0) { carsRelationsService.createCarsRelation(any(), any()) }
    }

    @Test
    fun `test createCarsRelations circular blocking`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 1L,
            blockedCarId = 2L,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        
        every { blockingCar.id } returns 1L
        every { blockedCar.id } returns 2L
        every { carService.getCarById(1L) } returns blockingCar
        every { carService.getCarById(2L) } returns blockedCar
        every { carsRelationsService.wouldCreateCircularBlocking(blockingCar, blockedCar) } returns true

        // When & Then
        assertThrows<IllegalArgumentException> {
            mainService.createCarsRelations(carsRelationRequestDTO)
        }
        verify { 
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.wouldCreateCircularBlocking(any(), any())
        }
        verify(exactly = 0) { carsRelationsService.createCarsRelation(any(), any()) }
    }

    @Test
    fun `test deleteCarsRelations success`() {
        // Given
        val carsRelationRequestDTO = TestObjectBuilder.getCarsRelationRequestDTO(
            blockingCarId = 1L,
            blockedCarId = 2L,
            userCarSituation = UserCarSituation.IS_BLOCKING
        )
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val carRelationsDTO = TestObjectBuilder.getCarRelationsDTO()
        val carUsersDTO = TestObjectBuilder.getCarUsersDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        
        every { blockingCar.id } returns 1L
        every { blockedCar.id } returns 2L
        every { blockingCar.plateNumber } returns "BLOCKER"
        every { blockedCar.plateNumber } returns "BLOCKED"
        every { carService.getCarById(1L) } returns blockingCar
        every { carService.getCarById(2L) } returns blockedCar
        every { carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar) } just Runs
        every { carsRelationsService.findCarRelationsByCar(any(), any()) } returns carRelationsDTO
        every { userCarService.getCarUsersByCar(any()) } returns carUsersDTO
        every { userService.getUserById(any()) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        val result = mainService.deleteCarsRelations(carsRelationRequestDTO)

        // Then
        assertNotNull(result)
        assertEquals(carRelationsDTO.car.id, result.car.id)
        verify { 
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.deleteSpecificCarsRelation(blockingCar, blockedCar)
            carsRelationsService.findCarRelationsByCar(any(), any())
        }
    }

    @Test
    fun `test deleteAllCarRelationsByCarId success`() {
        // Given
        val carId = 1L
        val car = TestObjectBuilder.getCar(id = carId)
        val blockedCar1 = TestObjectBuilder.getCar(id = 2L)
        val blockedCar2 = TestObjectBuilder.getCar(id = 3L)
        val carRelations = TestObjectBuilder.getCarRelations(
            car = car,
            isBlocking = listOf(blockedCar1, blockedCar2)
        )
        val carUsersDTO = TestObjectBuilder.getCarUsersDTO()
        val user = TestObjectBuilder.getUser()
        
        every { carService.getCarById(carId) } returns car
        every { carsRelationsService.findCarRelations(car) } returns carRelations
        every { carsRelationsService.deleteAllCarsRelations(car) } just Runs
        every { userCarService.getCarUsersByCar(any()) } returns carUsersDTO
        every { userService.getUserById(any()) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        mainService.deleteAllCarRelationsByCarId(carId)

        // Then
        verify { 
            carService.getCarById(carId)
            carsRelationsService.findCarRelations(car)
            carsRelationsService.deleteAllCarsRelations(car)
        }
    }

    @Test
    fun `test sendNeedToGoNotification success`() {
        // Given
        val blockedCarId = 1L
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val carRelations = TestObjectBuilder.getCarRelations(
            car = blockedCar,
            isBlockedBy = listOf(blockingCar)
        )
        val carUsersDTO = TestObjectBuilder.getCarUsersDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        
        every { blockedCar.id } returns blockedCarId
        every { blockingCar.id } returns 2L
        every { carService.getCarById(blockedCarId) } returns blockedCar
        every { carsRelationsService.findCarRelations(any()) } returns carRelations
        every { userCarService.getCarUsersByCar(any()) } returns carUsersDTO
        every { userService.getUserById(any()) } returns user
        every { notificationService.sendPushNotification(any(), any()) } just Runs

        // When
        mainService.sendNeedToGoNotification(blockedCarId)

        // Then
        verify { 
            carService.getCarById(blockedCarId)
            carsRelationsService.findCarRelations(any())
            userCarService.getCarUsersByCar(any())
            userService.getUserById(any())
            notificationService.sendPushNotification(any(), any())
        }
    }

    @Test
    fun `test sendNeedToGoNotification no blocking cars`() {
        // Given
        val blockedCarId = 1L
        val blockedCar = TestObjectBuilder.getCar(id = blockedCarId)
        val carRelations = TestObjectBuilder.getCarRelations(
            car = blockedCar,
            isBlockedBy = emptyList()
        )
        
        every { carService.getCarById(blockedCarId) } returns blockedCar
        every { carsRelationsService.findCarRelations(blockedCar) } returns carRelations

        // When & Then
        assertThrows<com.yb.rh.error.RHException> {
            mainService.sendNeedToGoNotification(blockedCarId)
        }
        
        verify { 
            carService.getCarById(blockedCarId)
            carsRelationsService.findCarRelations(blockedCar)
        }
        verify(exactly = 0) { 
            userCarService.getCarUsersByCar(any())
            userService.getUserById(any())
            notificationService.sendPushNotification(any(), any())
        }
    }

    @Test
    fun `test sendNeedToGoNotification no users in blocking car`() {
        // Given
        val blockedCarId = 1L
        val blockedCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val blockingCar = mockk<com.yb.rh.entities.Car>(relaxed = true)
        val carRelations = TestObjectBuilder.getCarRelations(
            car = blockedCar,
            isBlockedBy = listOf(blockingCar)
        )
        val emptyCarUsersDTO = TestObjectBuilder.getEmptyCarUsersDTO()
        
        every { blockedCar.id } returns blockedCarId
        every { blockingCar.id } returns 2L
        every { carService.getCarById(blockedCarId) } returns blockedCar
        every { carsRelationsService.findCarRelations(any()) } returns carRelations
        every { userCarService.getCarUsersByCar(any()) } returns emptyCarUsersDTO

        // When & Then
        assertThrows<com.yb.rh.error.RHException> {
            mainService.sendNeedToGoNotification(blockedCarId)
        }

        // Then
        verify { 
            carService.getCarById(blockedCarId)
            carsRelationsService.findCarRelations(any())
            userCarService.getCarUsersByCar(any())
        }
        verify(exactly = 0) { 
            userService.getUserById(any())
            notificationService.sendPushNotification(any(), any())
        }
    }

    @Test
    fun `test getActualUserCar when user is blocked`() {
        val blockingCar = TestObjectBuilder.getCar(id = 1L)
        val blockedCar = TestObjectBuilder.getCar(id = 2L)

        val result = mainService.getActualUserCar(UserCarSituation.IS_BLOCKED, blockingCar, blockedCar)

        assertEquals(blockedCar.id, result.id)
    }

    @Test
    fun `test getActualUserCar when user is blocking`() {
        val blockingCar = TestObjectBuilder.getCar(id = 1L)
        val blockedCar = TestObjectBuilder.getCar(id = 2L)

        val result = mainService.getActualUserCar(UserCarSituation.IS_BLOCKING, blockingCar, blockedCar)

        assertEquals(blockingCar.id, result.id)
    }

    @Test
    fun `test getUserCarRelations success with multiple cars`() {
        // Given
        val currentUser = TestObjectBuilder.getUser(userId = 1L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO(
            user = TestObjectBuilder.getUserDTO(id = 1L),
            cars = listOf(
                TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"),
                TestObjectBuilder.getCarDTO(id = 2L, plateNumber = "CAR2")
            )
        )
        val car1 = TestObjectBuilder.getCar(id = 1L, plateNumber = "CAR1")
        val car2 = TestObjectBuilder.getCar(id = 2L, plateNumber = "CAR2")
        val carRelationsDTO1 = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"),
            isBlocking = listOf(TestObjectBuilder.getCarDTO(id = 3L, plateNumber = "BLOCKED1")),
            isBlockedBy = emptyList()
        )
        val carRelationsDTO2 = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 2L, plateNumber = "CAR2"),
            isBlocking = emptyList(),
            isBlockedBy = listOf(TestObjectBuilder.getCarDTO(id = 4L, plateNumber = "BLOCKER1"))
        )

        every { currentUserService.getCurrentUser() } returns currentUser
        every { userCarService.getUserCarsByUser(currentUser) } returns userCarsDTO
        every { carService.getCarById(1L) } returns car1
        every { carService.getCarById(2L) } returns car2
        every { carsRelationsService.findCarRelationsByCar(car1) } returns carRelationsDTO1
        every { carsRelationsService.findCarRelationsByCar(car2) } returns carRelationsDTO2

        // When
        val result = mainService.getUserCarRelations()

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(carRelationsDTO1.car.id, result[0].car.id)
        assertEquals(carRelationsDTO2.car.id, result[1].car.id)
        verify { 
            currentUserService.getCurrentUser()
            userCarService.getUserCarsByUser(currentUser)
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.findCarRelationsByCar(car1)
            carsRelationsService.findCarRelationsByCar(car2)
        }
    }

    @Test
    fun `test getUserCarRelations success with single car`() {
        // Given
        val currentUser = TestObjectBuilder.getUser(userId = 1L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO(
            user = TestObjectBuilder.getUserDTO(id = 1L),
            cars = listOf(TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"))
        )
        val car1 = TestObjectBuilder.getCar(id = 1L, plateNumber = "CAR1")
        val carRelationsDTO1 = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"),
            isBlocking = listOf(TestObjectBuilder.getCarDTO(id = 2L, plateNumber = "BLOCKED1")),
            isBlockedBy = listOf(TestObjectBuilder.getCarDTO(id = 3L, plateNumber = "BLOCKER1"))
        )

        every { currentUserService.getCurrentUser() } returns currentUser
        every { userCarService.getUserCarsByUser(currentUser) } returns userCarsDTO
        every { carService.getCarById(1L) } returns car1
        every { carsRelationsService.findCarRelationsByCar(car1) } returns carRelationsDTO1

        // When
        val result = mainService.getUserCarRelations()

        // Then
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(carRelationsDTO1.car.id, result[0].car.id)
        assertEquals(carRelationsDTO1.isBlocking.size, result[0].isBlocking.size)
        assertEquals(carRelationsDTO1.isBlockedBy.size, result[0].isBlockedBy.size)
        verify { 
            currentUserService.getCurrentUser()
            userCarService.getUserCarsByUser(currentUser)
            carService.getCarById(1L)
            carsRelationsService.findCarRelationsByCar(car1)
        }
    }

    @Test
    fun `test getUserCarRelations success with no cars`() {
        // Given
        val currentUser = TestObjectBuilder.getUser(userId = 1L)
        val userCarsDTO = TestObjectBuilder.getEmptyUserCarsDTO()

        every { currentUserService.getCurrentUser() } returns currentUser
        every { userCarService.getUserCarsByUser(currentUser) } returns userCarsDTO

        // When
        val result = mainService.getUserCarRelations()

        // Then
        assertNotNull(result)
        assertEquals(0, result.size)
        verify { 
            currentUserService.getCurrentUser()
            userCarService.getUserCarsByUser(currentUser)
        }
        verify(exactly = 0) { 
            carService.getCarById(any())
            carsRelationsService.findCarRelationsByCar(any())
        }
    }

    @Test
    fun `test getUserCarRelations with cars having no relations`() {
        // Given
        val currentUser = TestObjectBuilder.getUser(userId = 1L)
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO(
            user = TestObjectBuilder.getUserDTO(id = 1L),
            cars = listOf(
                TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"),
                TestObjectBuilder.getCarDTO(id = 2L, plateNumber = "CAR2")
            )
        )
        val car1 = TestObjectBuilder.getCar(id = 1L, plateNumber = "CAR1")
        val car2 = TestObjectBuilder.getCar(id = 2L, plateNumber = "CAR2")
        val carRelationsDTO1 = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 1L, plateNumber = "CAR1"),
            isBlocking = emptyList(),
            isBlockedBy = emptyList()
        )
        val carRelationsDTO2 = TestObjectBuilder.getCarRelationsDTO(
            car = TestObjectBuilder.getCarDTO(id = 2L, plateNumber = "CAR2"),
            isBlocking = emptyList(),
            isBlockedBy = emptyList()
        )

        every { currentUserService.getCurrentUser() } returns currentUser
        every { userCarService.getUserCarsByUser(currentUser) } returns userCarsDTO
        every { carService.getCarById(1L) } returns car1
        every { carService.getCarById(2L) } returns car2
        every { carsRelationsService.findCarRelationsByCar(car1) } returns carRelationsDTO1
        every { carsRelationsService.findCarRelationsByCar(car2) } returns carRelationsDTO2

        // When
        val result = mainService.getUserCarRelations()

        // Then
        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals(0, result[0].isBlocking.size)
        assertEquals(0, result[0].isBlockedBy.size)
        assertEquals(0, result[1].isBlocking.size)
        assertEquals(0, result[1].isBlockedBy.size)
        verify { 
            currentUserService.getCurrentUser()
            userCarService.getUserCarsByUser(currentUser)
            carService.getCarById(1L)
            carService.getCarById(2L)
            carsRelationsService.findCarRelationsByCar(car1)
            carsRelationsService.findCarRelationsByCar(car2)
        }
    }
} 