package controllers.simple

import com.github.michaelbull.result.Ok
import com.yb.rh.controllers.UsersController
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UsersService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * Simple test for UsersController with minimal mocking
 */
class UsersControllerBasicTest {

    @Test
    fun `findAll should return all users from service`() {
        // Given
        val usersService = mockk<UsersService>()
        val usersController = UsersController(usersService)
        
        val testUser = mockk<User>()
        val usersList = listOf(testUser)
        
        every { usersService.findAll() } returns usersList
        
        // When
        val response = usersController.findAll()
        
        // Then
        assertEquals(usersList, response)
        verify { usersService.findAll() }
    }
    
    @Test
    fun `findById should return user by ID when found`() {
        // Given
        val usersService = mockk<UsersService>()
        val usersController = UsersController(usersService)
        
        val testUserDTO = mockk<UserDTO>()
        val userId = 1L
        
        every { usersService.findByUserId(userId) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.findById(userId)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { usersService.findByUserId(userId) }
    }
    
    @Test
    fun `findByEmail should return user by email when found`() {
        // Given
        val usersService = mockk<UsersService>()
        val usersController = UsersController(usersService)
        
        val testUserDTO = mockk<UserDTO>()
        val email = "test@example.com"
        
        every { usersService.findByEmail(email) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.findByEmail(email)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { usersService.findByEmail(email) }
    }
    
    @Test
    fun `createOrUpdateUser should create or update user`() {
        // Given
        val usersService = mockk<UsersService>()
        val usersController = UsersController(usersService)
        
        val testUserDTO = mockk<UserDTO>()
        every { testUserDTO.email } returns "test@example.com"
        
        every { usersService.createOrUpdateUser(testUserDTO) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.createOrUpdateUser(testUserDTO)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { usersService.createOrUpdateUser(testUserDTO) }
    }
} 