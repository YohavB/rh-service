package services

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.error.EntityNotFound
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import com.yb.rh.services.UserService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UsersServiceTest {

    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private lateinit var userCarRepository: UserCarRepository

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null,
        userId = 1L
    )

    private val testUserDTO = UserDTO(
        id = 1L,
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null,
        userCars = null
    )
    
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

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        // Create mocks
        userRepository = mockk(relaxed = true)
        userCarRepository = mockk(relaxed = true)
        
        // Create the service with mock dependencies
        userService = mockk(relaxed = true)
    }

    @Test
    fun `test find all users`() {
        // Given
        val usersList = listOf(testUser)
        every { userService.findAll() } returns usersList
        
        // When
        val result = userService.findAll()
        
        // Then
        assertEquals(1, result.size)
        assertEquals(testUser, result[0])
        verify(exactly = 1) { userService.findAll() }
    }

    @Test
    fun `test find by user id success`() {
        // Given
        every { userService.findByUserId(1L) } returns Ok(testUserDTO)
        
        // When
        val result = userService.findByUserId(1L)
        
        // Then
        assertTrue(result is Ok)
        val userData = result.value
        assertEquals(testUser.userId, userData.id)
        assertEquals(testUser.email, userData.email)
        
        verify(exactly = 1) { userService.findByUserId(1L) }
    }
    
    @Test
    fun `test find by user id not found`() {
        // Given
        every { userService.findByUserId(999L) } returns Err(EntityNotFound(User::class.java, "999"))
        
        // When
        val result = userService.findByUserId(999L)
        
        // Then
        assertTrue(result is Err)
        verify(exactly = 1) { userService.findByUserId(999L) }
    }

    @Test
    fun `test find by email success`() {
        // Given
        every { userService.findByEmail(testUser.email) } returns Ok(testUserDTO)
        
        // When
        val result = userService.findByEmail(testUser.email)
        
        // Then
        assertTrue(result is Ok)
        val userData = result.value
        assertEquals(testUser.userId, userData.id)
        assertEquals(testUser.email, userData.email)
        
        verify(exactly = 1) { userService.findByEmail(testUser.email) }
    }
    
    @Test
    fun `test find by email not found`() {
        // Given
        val unknownEmail = "unknown@test.com"
        every { userService.findByEmail(unknownEmail) } returns Err(EntityNotFound(User::class.java, unknownEmail))
        
        // When
        val result = userService.findByEmail(unknownEmail)
        
        // Then
        assertTrue(result is Err)
        verify(exactly = 1) { userService.findByEmail(unknownEmail) }
    }

    @Test
    fun `test create or update user success`() {
        // Given
        val userToCreate = testUserDTO.copy(id = 0L) // New user without ID
        every { userService.createOrUpdateUser(userToCreate) } returns Ok(testUserDTO)
        
        // When
        val result = userService.createOrUpdateUser(userToCreate)
        
        // Then
        assertTrue(result is Ok)
        val userData = result.value
        assertEquals(testUserDTO.id, userData.id)
        assertEquals(testUserDTO.email, userData.email)
        
        verify(exactly = 1) { userService.createOrUpdateUser(userToCreate) }
    }
} 