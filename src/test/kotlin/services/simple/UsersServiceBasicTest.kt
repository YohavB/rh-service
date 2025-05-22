package services.simple

import com.yb.rh.entities.User
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.UsersService
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
        val usersRepository = mockk<UsersRepository>()
        val usersCarsRepository = mockk<UsersCarsRepository>()
        
        val testUser = mockk<User>()
        val usersList = listOf(testUser)
        
        every { usersRepository.findAll() } returns usersList
        
        val usersService = UsersService(usersRepository, usersCarsRepository)
        
        // When
        val result = usersService.findAll()
        
        // Then
        assertEquals(usersList, result)
        verify { usersRepository.findAll() }
    }
} 