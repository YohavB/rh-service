package repositories

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.yb.rh.entities.User
import com.yb.rh.error.EntityNotFound
import com.yb.rh.error.GetDbRecordFailed
import com.yb.rh.error.SaveDbRecordFailed
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.repositories.findByUserIdSafe
import com.yb.rh.repositories.findByEmailSafe
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

class UsersRepositoriesTest {
    
    private lateinit var usersRepository: UsersRepository
    
    private val testUser = User(
        userId = 1L,
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        pushNotificationToken = "token123",
        urlPhoto = null
    )
    
    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersRepository = mockk(relaxed = true)
    }
    
    @Test
    fun `test saveSafe success`() {
        // Setup mock
        every { usersRepository.save(testUser) } returns testUser
        
        // Execute the function
        val result = usersRepository.saveSafe(testUser)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testUser, (result as Ok).value)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.save(testUser) }
    }
    
    @Test
    fun `test saveSafe failure`() {
        // Setup mock for failure
        every { usersRepository.save(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = usersRepository.saveSafe(testUser)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is SaveDbRecordFailed)
        assertEquals("Failed to save a record to table `users`", result.error.message)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.save(any()) }
    }
    
    @Test
    fun `test findByUserIdSafe success`() {
        // Setup mock
        every { usersRepository.findByUserId(testUser.userId) } returns testUser
        
        // Execute the function
        val result = usersRepository.findByUserIdSafe(testUser.userId)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testUser, (result as Ok).value)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByUserId(testUser.userId) }
    }
    
    @Test
    fun `test findByUserIdSafe not found`() {
        // Setup mock for not found
        every { usersRepository.findByUserId(testUser.userId) } returns null
        
        // Execute the function
        val result = usersRepository.findByUserIdSafe(testUser.userId)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is EntityNotFound)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByUserId(testUser.userId) }
    }
    
    @Test
    fun `test findByUserIdSafe repository error`() {
        // Setup mock for database error
        every { usersRepository.findByUserId(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = usersRepository.findByUserIdSafe(testUser.userId)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users`", result.error.message)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByUserId(any()) }
    }
    
    @Test
    fun `test findByEmailSafe success`() {
        // Setup mock
        every { usersRepository.findByEmail(testUser.email) } returns testUser
        
        // Execute the function
        val result = usersRepository.findByEmailSafe(testUser.email)
        
        // Verify result
        assertTrue(result is Ok)
        assertEquals(testUser, (result as Ok).value)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByEmail(testUser.email) }
    }
    
    @Test
    fun `test findByEmailSafe not found`() {
        // Setup mock for not found
        every { usersRepository.findByEmail(testUser.email) } returns null
        
        // Execute the function
        val result = usersRepository.findByEmailSafe(testUser.email)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is EntityNotFound)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByEmail(testUser.email) }
    }
    
    @Test
    fun `test findByEmailSafe repository error`() {
        // Setup mock for database error
        every { usersRepository.findByEmail(any()) } throws RuntimeException("Database error")
        
        // Execute the function
        val result = usersRepository.findByEmailSafe(testUser.email)
        
        // Verify result
        assertTrue(result is Err)
        assertTrue((result as Err).error is GetDbRecordFailed)
        assertEquals("Failed to get a record from table `users`", result.error.message)
        
        // Verify repository method was called
        verify(exactly = 1) { usersRepository.findByEmail(any()) }
    }
} 