package repositories

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.SaveDbRecordFailed
import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.findByPlateNumberSafe
import com.yb.rh.repositories.saveSafe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertTrue

class CarsRepositoriesTest {
    
    private lateinit var carRepository: CarRepository
    
    private val testCar = Car(
        plateNumber = "123456",
        country = Countries.IL,
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )
    
    @BeforeEach
    fun setup() {
        clearAllMocks()
        carRepository = mockk(relaxed = true)
    }
    
    @Test
    fun `test saveSafe success`() {
        // Setup mock
        every { carRepository.save(testCar) } returns testCar
        
        // Execute the function
        val result = carRepository.saveSafe(testCar)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testCar, (result as Ok).value)
        
        // Verify repository method was called
        verify(exactly = 1) { carRepository.save(testCar) }
    }
    
    @Test
    fun `test saveSafe failure`() {
        // Setup mock for failure
        every { carRepository.save(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = carRepository.saveSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is SaveDbRecordFailed)
        assertEquals("Failed to save a record to table `cars`", result.error.message)
        
        // Verify repository method was called
        verify(exactly = 1) { carRepository.save(any()) }
    }
    
    @Test
    fun `test findByPlateNumberSafe success`() {
        // Setup mock
        every { carRepository.findByPlateNumber(testCar.plateNumber) } returns testCar
        
        // Execute the function
        val result = carRepository.findByPlateNumberSafe(testCar.plateNumber)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testCar, (result as Ok).value)
        
        // Verify repository method was called
        verify(exactly = 1) { carRepository.findByPlateNumber(testCar.plateNumber) }
    }
    
    @Test
    fun `test findByPlateNumberSafe not found`() {
        // Setup mock for not found
        every { carRepository.findByPlateNumber(testCar.plateNumber) } returns null
        
        // Execute the function
        val result = carRepository.findByPlateNumberSafe(testCar.plateNumber)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is EntityNotFound)
        
        // Verify repository method was called
        verify(exactly = 1) { carRepository.findByPlateNumber(testCar.plateNumber) }
    }
    
    @Test
    fun `test findByPlateNumberSafe repository error`() {
        // Setup mock for database error
        every { carRepository.findByPlateNumber(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = carRepository.findByPlateNumberSafe(testCar.plateNumber)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `cars`", result.error.message)
        
        // Verify repository method was called
        verify(exactly = 1) { carRepository.findByPlateNumber(any()) }
    }
} 