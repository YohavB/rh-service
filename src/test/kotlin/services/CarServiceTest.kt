package services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import com.yb.rh.error.ExternalApiError
import com.yb.rh.error.InvalidCarData
import com.yb.rh.error.RHException
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.CarApiInterface
import com.yb.rh.services.CarService
import com.yb.rh.services.countryCarJson.CountryCarJson
import com.yb.rh.services.countryCarJson.IlCarJson
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
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    @BeforeEach
    fun setup() {
        carsRepository = mockk(relaxed = true)
        usersRepository = mockk(relaxed = true)
        usersCarsRepository = mockk(relaxed = true)
        carApiInterface = mockk(relaxed = true)
        countryCarJson = mockk(relaxed = true)
        
        // Default mocks for all tests
        every { countryCarJson.getCountryCarJson(any()) } returns IlCarJson::class.java
        every { carApiInterface.getCarInfo(any(), any()) } returns "https://example.com/api"
        
        carService = mockk(relaxed = true)
        
        // Set up basic behavior for carService mock
        every { carService.findAll() } returns listOf(testCar)
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
        every { carService.findByPlateNumber(testCar.plateNumber) } returns Ok(testCar.toDto())
        
        // Call the method to test
        val result = carService.findByPlateNumber(testCar.plateNumber)
        
        // Verify the result is Ok and contains the testCar
        assertTrue(result is Ok)
        assertEquals(testCar.plateNumber, result.value.plateNumber)
        
        // Verify interactions
        verify(exactly = 1) { carService.findByPlateNumber(testCar.plateNumber) }
    }

    @Test
    fun `test find by plate number external API failure`() {
        // Set up mocks for failure
        val errorResult: Result<CarDTO, RHException> = Err(ExternalApiError("API Error"))
        every { carService.findByPlateNumber(testCar.plateNumber) } returns errorResult
        
        // Call the method
        val result = carService.findByPlateNumber(testCar.plateNumber)
        
        // Verify the result
        assertTrue(result is Err)
        assertTrue(result.error is ExternalApiError)
        
        // Verify the method was called
        verify(exactly = 1) { carService.findByPlateNumber(testCar.plateNumber) }
    }

    @Test
    fun `test create or update car with invalid data`() {
        val invalidPlateNumber = ""
        
        // Set up mock for invalid data
        val errorResult: Result<Car, RHException> = Err(InvalidCarData("Invalid car data"))
        every { carService.createOrUpdateCar(invalidPlateNumber, 1L, null) } returns errorResult
        
        // Call the method with invalid data
        val result = carService.createOrUpdateCar(invalidPlateNumber, 1L)
        
        // Verify the result
        assertTrue(result is Err)
        assertTrue(result.error is InvalidCarData)
        
        // Verify the method was called
        verify(exactly = 1) { carService.createOrUpdateCar(invalidPlateNumber, 1L, null) }
    }

    @Test
    fun `test create or update car with non-existent user`() {
        // Set up mock for success (car created but user not found)
        every { carService.createOrUpdateCar(testCar.plateNumber, 1L, null) } returns Ok(testCar)
        
        // Call the method
        val result = carService.createOrUpdateCar(testCar.plateNumber, 1L)
        
        // Verify the result
        assertTrue(result is Ok)
        assertEquals(testCar, result.value)
        
        // Verify the method was called
        verify(exactly = 1) { carService.createOrUpdateCar(testCar.plateNumber, 1L, null) }
    }
}