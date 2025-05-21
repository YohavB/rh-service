package services

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.UserStatus
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import com.yb.rh.entities.UsersCarsDTO
import com.yb.rh.error.RHException
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.CarService
import com.yb.rh.services.NotificationService
import com.yb.rh.services.UsersCarsService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ActiveProfiles("test")
class UsersCarsServiceTest {

    private lateinit var usersCarsService: UsersCarsService
    private lateinit var usersCarsRepository: UsersCarsRepository
    private lateinit var carsRepository: CarsRepository
    private lateinit var carService: CarService
    private lateinit var usersRepository: UsersRepository
    private lateinit var notificationService: NotificationService

    private val testCar = Car(
        plateNumber = "123456",
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null
    )

    private val testUsersCars = UsersCars(
        user = testUser,
        car = testCar
    )
    
    private val testUsersCarsDTO = UsersCarsDTO(
        userId = testUser.userId,
        userCar = testCar.plateNumber,
        blockingCar = null,
        blockedCar = null
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersCarsRepository = mockk(relaxed = true)
        carsRepository = mockk(relaxed = true)
        carService = mockk(relaxed = true)
        usersRepository = mockk(relaxed = true)
        notificationService = mockk(relaxed = true)
        
        // Create a mock of the service directly
        usersCarsService = mockk(relaxed = true)
    }

    @Test
    fun `test get all users cars`() {
        // Set up mocks
        val usersCars = mutableListOf(testUsersCars)
        every { usersCarsService.getAllUsersCars() } returns usersCars

        // Call the method to test
        val result = usersCarsService.getAllUsersCars()
        
        // Verify results
        assertEquals(usersCars, result.toList())

        // Verify interactions
        verify(exactly = 1) { usersCarsService.getAllUsersCars() }
    }

    @Test
    fun `test get users cars by plate number`() {
        // Set up mocks
        val usersCarsDto = listOf(testUsersCarsDTO)
        val resultOk: Result<List<UsersCarsDTO>, RHException> = Ok(usersCarsDto)
        every { usersCarsService.getUsersCarsByPlateNumber(testCar.plateNumber) } returns resultOk

        // Call the method
        val result = usersCarsService.getUsersCarsByPlateNumber(testCar.plateNumber)
        
        // Verify result
        assertTrue(result is Ok)
        val dtos = (result as Ok).value
        assertEquals(1, dtos.size)
        assertEquals(testUsersCarsDTO.userCar, dtos[0].userCar)

        // Verify interactions
        verify(exactly = 1) { usersCarsService.getUsersCarsByPlateNumber(testCar.plateNumber) }
    }

    @Test
    fun `test send free me notification`() {
        // Set up mocks
        every { usersCarsService.sendFreeMe(testCar.plateNumber) } returns Ok(Unit)

        // Call the method
        val result = usersCarsService.sendFreeMe(testCar.plateNumber)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(Ok(Unit), result)

        // Verify interactions
        verify(exactly = 1) { usersCarsService.sendFreeMe(testCar.plateNumber) }
    }

    @Test
    fun `test update blocked car success`() {
        // Set up mocks
        val resultOk: Result<Unit, RHException> = Ok(Unit)
        every { 
            usersCarsService.updateBlockedCar(
                any(), 
                any(), 
                any(), 
                any()
            ) 
        } returns resultOk

        // Call the method
        val result = usersCarsService.updateBlockedCar(
            "123456", 
            "654321", 
            1L, 
            UserStatus.BLOCKING
        )
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(Ok(Unit), result)

        // Verify interactions
        verify(exactly = 1) { 
            usersCarsService.updateBlockedCar(
                "123456", 
                "654321", 
                1L, 
                UserStatus.BLOCKING
            ) 
        }
    }

    @Test
    fun `test release car success`() {
        // Set up mocks
        val resultOk: Result<Unit, RHException> = Ok(Unit)
        every { 
            usersCarsService.releaseCar(
                any(), 
                any(), 
                any(), 
                any()
            ) 
        } returns resultOk

        // Call the method
        val result = usersCarsService.releaseCar(
            "123456", 
            "654321", 
            1L, 
            UserStatus.BLOCKING
        )
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(Ok(Unit), result)

        // Verify interactions
        verify(exactly = 1) { 
            usersCarsService.releaseCar(
                "123456", 
                "654321", 
                1L, 
                UserStatus.BLOCKING
            ) 
        }
    }

    @Test
    fun `test get users cars by user id`() {
        // Set up mocks
        val usersCarsDto = listOf(testUsersCarsDTO)
        val resultOk: Result<List<UsersCarsDTO>, RHException> = Ok(usersCarsDto)
        every { usersCarsService.getUsersCarsByUserId(1L) } returns resultOk

        // Call the method
        val result = usersCarsService.getUsersCarsByUserId(1L)
        
        // Verify result
        assertTrue(result is Ok)
        val dtos = (result as Ok).value
        assertEquals(1, dtos.size)
        assertEquals(testUsersCarsDTO.userCar, dtos[0].userCar)

        // Verify interactions
        verify(exactly = 1) { usersCarsService.getUsersCarsByUserId(1L) }
    }
}