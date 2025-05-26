package services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.CarStatus
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.common.UserStatus
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import com.yb.rh.entities.User
import com.yb.rh.error.EntityNotFound
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.CarApiInterface
import com.yb.rh.services.CarService
import com.yb.rh.services.countryCarJson.CountryCarJson
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import kotlin.test.assertTrue

@ActiveProfiles("test")
class CarServiceTest {

    private lateinit var carService: CarService
    private lateinit var carsRepository: CarsRepository
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersCarsRepository: UsersCarsRepository
    private lateinit var carApiInterface: CarApiInterface
    private lateinit var countryCarJson: CountryCarJson

    private val testCar = Car(
        plateNumber = "123456",
        country = Countries.IL,
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    private val testCarDTO = CarDTO(
        plateNumber = "123456",
        country = Countries.IL,
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
        urlPhoto = null,
        userId = 1L
    )

    @BeforeEach
    fun setup() {
        carsRepository = mockk(relaxed = true)
        usersRepository = mockk(relaxed = true)
        usersCarsRepository = mockk(relaxed = true)
        carApiInterface = mockk(relaxed = true)
        countryCarJson = mockk(relaxed = true)

        // Create the actual service with mocked dependencies
        carService = mockk(relaxed = true)
    }

    @Test
    fun `test find all cars`() {
        // Set up specific mocks for this test
        val carsList = listOf(testCar)
        every { carService.findAll() } returns carsList

        // Call the method to test
        val result = carService.findAll()

        // Verify results
        assertEquals(carsList, result)

        // Verify interactions
        verify(exactly = 1) { carService.findAll() }
    }

    @Test
    fun `test find by plate number success from database`() {
        // Set up specific mocks for this test
        every { carService.findByPlateNumber(testCar.plateNumber, Countries.IL) } returns Ok(testCarDTO)

        // Call the method to test
        val result = carService.findByPlateNumber(testCar.plateNumber, Countries.IL)

        // Verify the result is Ok and contains the testCar
        assertTrue(result is Ok)
        assertEquals(testCar.plateNumber, result.value.plateNumber)

        // Verify interactions
        verify(exactly = 1) { carService.findByPlateNumber(testCar.plateNumber, Countries.IL) }
    }

    @Test
    fun `test find by plate number not found`() {
        // Set up mocks for not found
        every { carService.findByPlateNumber(testCar.plateNumber, Countries.IL) } returns
                Err(EntityNotFound(Car::class.java, testCar.plateNumber))

        // Call the method
        val result = carService.findByPlateNumber(testCar.plateNumber, Countries.IL)

        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is EntityNotFound)

        // Verify API was called
        verify { carService.findByPlateNumber(testCar.plateNumber, Countries.IL) }
    }

    @Test
    fun `test create or update car success`() {
        // Set up mocks for successful car creation
        every { carService.createOrUpdateCar(testCar.plateNumber, Countries.IL, 1L, null) } returns Ok(testCarDTO)

        // Call the method
        val result = carService.createOrUpdateCar(testCar.plateNumber, Countries.IL, 1L)

        // Verify the result
        assertTrue(result is Ok)
        assertEquals(testCarDTO, result.value)

        // Verify the method was called
        verify(exactly = 1) { carService.createOrUpdateCar(testCar.plateNumber, Countries.IL, 1L, null) }
    }

    @Test
    fun `test find and update car success`() {
        // Set up mocks for success
        every {
            carService.findAndUpdateCar(
                testCar.plateNumber,
                1L,
                UserStatus.BLOCKING,
                true,
                CarStatus.BLOCKING
            )
        } returns Ok(testCar)

        // Call the method
        val result = carService.findAndUpdateCar(
            testCar.plateNumber,
            1L,
            UserStatus.BLOCKING,
            true,
            CarStatus.BLOCKING
        )

        // Verify result
        assertTrue(result is Ok)
        assertEquals(testCar, result.value)

        // Verify method was called
        verify(exactly = 1) {
            carService.findAndUpdateCar(
                testCar.plateNumber,
                1L,
                UserStatus.BLOCKING,
                true,
                CarStatus.BLOCKING
            )
        }
    }
}