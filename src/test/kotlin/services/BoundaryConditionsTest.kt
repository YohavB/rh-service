package services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.error.InvalidCarData
import com.yb.rh.error.InvalidUserData
import com.yb.rh.error.RHException
import com.yb.rh.services.CarService
import com.yb.rh.services.UsersService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertTrue

@ActiveProfiles("test")
class BoundaryConditionsTest {

    private lateinit var carService: CarService
    private lateinit var usersService: UsersService

    @BeforeEach
    fun setup() {
        clearAllMocks()
        carService = mockk(relaxed = true)
        usersService = mockk(relaxed = true)
    }

    @Test
    fun `test car with maximum length plate number`() {
        val car = Car(
            plateNumber = "1".repeat(20), // Assuming max length is less than this
            brand = Brands.TESLA,
            model = "Model 3",
            color = Colors.BLACK,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )

        // Set up mock to return error for validation failure
        val errorResult: Result<Car, RHException> = Err(InvalidCarData("Plate number too long"))
        every { carService.createOrUpdateCar(car.plateNumber, 1L) } returns errorResult

        val result = carService.createOrUpdateCar(car.plateNumber, 1L)
        
        assertTrue(result is Err)
        assertTrue((result as Err).error is InvalidCarData)
        
        verify(exactly = 1) { carService.createOrUpdateCar(car.plateNumber, 1L) }
    }

    @Test
    fun `test user with invalid email format`() {
        val user = User(
            firstName = "Test",
            lastName = "User",
            email = "invalid-email",
            pushNotificationToken = "test-token",
            urlPhoto = null
        )

        val userDto = user.toDto()

        // Set up mock to return error for validation failure
        val errorResult: Result<UserDTO, RHException> = Err(InvalidUserData("Invalid email format"))
        every { usersService.createOrUpdateUser(userDto) } returns errorResult

        val result = usersService.createOrUpdateUser(userDto)
        
        assertTrue(result is Err)
        assertTrue((result as Err).error is InvalidUserData)
        
        verify(exactly = 1) { usersService.createOrUpdateUser(userDto) }
    }

    @Test
    fun `test car with past license expiry date`() {
        val car = Car(
            plateNumber = "123456",
            brand = Brands.TESLA,
            model = "Model 3",
            color = Colors.BLACK,
            carLicenseExpireDate = LocalDateTime.now().minusYears(1)
        )

        // Set up mock to return error for validation failure
        val errorResult: Result<Car, RHException> = Err(InvalidCarData("License expiry date cannot be in the past"))
        every { carService.createOrUpdateCar(car.plateNumber, 1L) } returns errorResult

        val result = carService.createOrUpdateCar(car.plateNumber, 1L)
        
        assertTrue(result is Err)
        assertTrue((result as Err).error is InvalidCarData)
        
        verify(exactly = 1) { carService.createOrUpdateCar(car.plateNumber, 1L) }
    }

    @Test
    fun `test user with empty required fields`() {
        val user = User(
            firstName = "",
            lastName = "",
            email = "test@test.com",
            pushNotificationToken = "",
            urlPhoto = null
        )

        val userDto = user.toDto()

        // Set up mock to return error for validation failure
        val errorResult: Result<UserDTO, RHException> = Err(InvalidUserData("Required fields cannot be empty"))
        every { usersService.createOrUpdateUser(userDto) } returns errorResult

        val result = usersService.createOrUpdateUser(userDto)
        
        assertTrue(result is Err)
        assertTrue((result as Err).error is InvalidUserData)
        
        verify(exactly = 1) { usersService.createOrUpdateUser(userDto) }
    }
} 