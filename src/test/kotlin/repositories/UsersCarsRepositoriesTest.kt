package repositories

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.SaveDbRecordFailed
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.findByBlockedCarSafe
import com.yb.rh.repositories.findByBlockingCarSafe
import com.yb.rh.repositories.findByCarSafe
import com.yb.rh.repositories.findByUserAndCarSafe
import com.yb.rh.repositories.findByUserSafe
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

class UsersCarsRepositoriesTest {
    
    private lateinit var userCarRepository: UserCarRepository
    
    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null,
        userId = 1L
    )
    
    private val testCar = Car(
        plateNumber = "123456",
        country = Countries.IL,
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )
    
    private val testUsersCars = UsersCars(
        user = testUser,
        car = testCar
    )
    
    @BeforeEach
    fun setup() {
        clearAllMocks()
        userCarRepository = mockk(relaxed = true)
    }
    
    @Test
    fun `test saveSafe success`() {
        // Setup mock
        every { userCarRepository.save(testUsersCars) } returns testUsersCars
        
        // Execute the function
        val result = userCarRepository.saveSafe(testUsersCars)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testUsersCars, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.save(testUsersCars) }
    }
    
    @Test
    fun `test saveSafe failure`() {
        // Setup mock for failure
        every { userCarRepository.save(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.saveSafe(testUsersCars)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is SaveDbRecordFailed)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.save(any()) }
    }
    
    @Test
    fun `test findByUserSafe success`() {
        // Setup mock
        val userCarsList = listOf(testUsersCars)
        every { userCarRepository.findAllByUser(testUser) } returns userCarsList
        
        // Execute the function
        val result = userCarRepository.findByUserSafe(testUser)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(userCarsList, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByUser(testUser) }
    }
    
    @Test
    fun `test findByUserSafe not found`() {
        // Setup mock for not found
        every { userCarRepository.findAllByUser(testUser) } returns null
        
        // Execute the function
        val result = userCarRepository.findByUserSafe(testUser)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is EntityNotFound)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByUser(testUser) }
    }
    
    @Test
    fun `test findByUserSafe repository error`() {
        // Setup mock for database error
        every { userCarRepository.findAllByUser(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.findByUserSafe(testUser)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users_cars`", result.error.message)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByUser(any()) }
    }
    
    @Test
    fun `test findByCarSafe success`() {
        // Setup mock
        val userCarsList = listOf(testUsersCars)
        every { userCarRepository.findAllByCar(testCar) } returns userCarsList
        
        // Execute the function
        val result = userCarRepository.findByCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(userCarsList, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByCar(testCar) }
    }
    
    @Test
    fun `test findByCarSafe not found`() {
        // Setup mock for not found
        every { userCarRepository.findAllByCar(testCar) } returns null
        
        // Execute the function
        val result = userCarRepository.findByCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is EntityNotFound)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByCar(testCar) }
    }
    
    @Test
    fun `test findByCarSafe repository error`() {
        // Setup mock for database error
        every { userCarRepository.findAllByCar(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.findByCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users_cars`", result.error.message)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findAllByCar(any()) }
    }
    
    @Test
    fun `test findByUserAndCarSafe success`() {
        // Setup mock
        every { userCarRepository.findByUserAndCar(testUser, testCar) } returns testUsersCars
        
        // Execute the function
        val result = userCarRepository.findByUserAndCarSafe(testUser, testCar)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testUsersCars, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByUserAndCar(testUser, testCar) }
    }
    
    @Test
    fun `test findByUserAndCarSafe not found`() {
        // Setup mock for not found - UserAndCar should return null on not found
        every { userCarRepository.findByUserAndCar(testUser, testCar) } throws RuntimeException("Entity not found")
        
        // Execute the function
        val result = userCarRepository.findByUserAndCarSafe(testUser, testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByUserAndCar(testUser, testCar) }
    }
    
    @Test
    fun `test findByUserAndCarSafe repository error`() {
        // Setup mock for database error
        every { userCarRepository.findByUserAndCar(any(), any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.findByUserAndCarSafe(testUser, testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users_cars`", result.error.message)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByUserAndCar(any(), any()) }
    }
    
    @Test
    fun `test findByBlockingCarSafe success`() {
        // Setup mock
        val userCarsList = listOf(testUsersCars)
        every { userCarRepository.findByBlockingCar(testCar) } returns userCarsList
        
        // Execute the function
        val result = userCarRepository.findByBlockingCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(userCarsList, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockingCar(testCar) }
    }
    
    @Test
    fun `test findByBlockingCarSafe not found`() {
        // Setup mock for not found
        every { userCarRepository.findByBlockingCar(testCar) } returns null
        
        // Execute the function
        val result = userCarRepository.findByBlockingCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is EntityNotFound)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockingCar(testCar) }
    }
    
    @Test
    fun `test findByBlockingCarSafe repository error`() {
        // Setup mock for database error
        every { userCarRepository.findByBlockingCar(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.findByBlockingCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users_cars`", result.error.message)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockingCar(any()) }
    }
    
    @Test
    fun `test findByBlockedCarSafe success`() {
        // Setup mock
        val userCarsList = listOf(testUsersCars)
        every { userCarRepository.findByBlockedCar(testCar) } returns userCarsList
        
        // Execute the function
        val result = userCarRepository.findByBlockedCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(userCarsList, result.value)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockedCar(testCar) }
    }
    
    @Test
    fun `test findByBlockedCarSafe not found`() {
        // Setup mock for not found
        every { userCarRepository.findByBlockedCar(testCar) } returns null
        
        // Execute the function
        val result = userCarRepository.findByBlockedCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is EntityNotFound)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockedCar(testCar) }
    }
    
    @Test
    fun `test findByBlockedCarSafe repository error`() {
        // Setup mock for database error
        every { userCarRepository.findByBlockedCar(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = userCarRepository.findByBlockedCarSafe(testCar)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue(result.error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users_cars`", result.error.message)
        
        // Verify the repository method was called
        verify(exactly = 1) { userCarRepository.findByBlockedCar(any()) }
    }
} 