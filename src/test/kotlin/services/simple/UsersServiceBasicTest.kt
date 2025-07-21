package services.simple

import com.yb.rh.entities.User
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import com.yb.rh.services.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Basic test for UsersService that focuses on minimal functionality without complex mocking
 */
class UsersServiceBasicTest {

    @Test
    fun `findAll should return all users from repository`() {
        // Given
        val userRepository = mockk<UserRepository>()
        val userCarRepository = mockk<UserCarRepository>()
        
        val testUser = mockk<User>()
        val usersList = listOf(testUser)
        
        every { userRepository.findAll() } returns usersList
        
        val userService = UserService(userRepository, userCarRepository)
        
        // When
        val result = userService.findAll()
        
        // Then
        assertEquals(usersList, result)
        verify { userRepository.findAll() }
    }
} 