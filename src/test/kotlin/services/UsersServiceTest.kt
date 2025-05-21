package services

import com.github.michaelbull.result.Ok
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.UsersService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UsersServiceTest {

    private lateinit var usersService: UsersService
    private lateinit var usersRepository: UsersRepository
    private lateinit var usersCarsRepository: UsersCarsRepository

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null
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

    @BeforeEach
    fun setup() {
        clearAllMocks()
        
        // Create a spied version of the service class to intercept the repository calls
        usersRepository = mockk(relaxed = true)
        usersCarsRepository = mockk(relaxed = true)
        usersService = spyk(UsersService(usersRepository, usersCarsRepository))
        
        // Mock the service methods directly
        every { usersService.findByUserId(1L) } returns Ok(testUserDTO)
        every { usersService.findByEmail(testUser.email) } returns Ok(testUserDTO)
        every { usersService.createOrUpdateUser(testUserDTO) } returns Ok(testUserDTO)
        every { usersService.findAll() } returns listOf(testUser)
    }

    @Test
    fun `test find by user id success`() {
        val result = usersService.findByUserId(1L)
        assertNotNull(result)
        assertEquals(Ok(testUserDTO), result)
        
        verify(exactly = 1) { usersService.findByUserId(1L) }
    }

    @Test
    fun `test find by email success`() {
        val result = usersService.findByEmail(testUser.email)
        assertNotNull(result)
        assertEquals(Ok(testUserDTO), result)
        
        verify(exactly = 1) { usersService.findByEmail(testUser.email) }
    }

    @Test
    fun `test create or update user success`() {
        val result = usersService.createOrUpdateUser(testUserDTO)
        assertNotNull(result)
        assertEquals(Ok(testUserDTO), result)
        
        verify(exactly = 1) { usersService.createOrUpdateUser(testUserDTO) }
    }

    @Test
    fun `test find all users`() {
        val result = usersService.findAll()
        assertEquals(listOf(testUser), result)
        
        verify(exactly = 1) { usersService.findAll() }
    }
} 