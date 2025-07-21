package controllers.simple

import com.github.michaelbull.result.Ok
import com.yb.rh.controllers.UsersController
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UserService
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
        val userService = mockk<UserService>()
        val usersController = UsersController(userService)
        
        val testUser = mockk<User>()
        val usersList = listOf(testUser)
        
        every { userService.findAll() } returns usersList
        
        // When
        val response = usersController.findAll()
        
        // Then
        assertEquals(usersList, response)
        verify { userService.findAll() }
    }
    
    @Test
    fun `findById should return user by ID when found`() {
        // Given
        val userService = mockk<UserService>()
        val usersController = UsersController(userService)
        
        val testUserDTO = mockk<UserDTO>()
        val userId = 1L
        
        every { userService.findByUserId(userId) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.findById(userId)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { userService.findByUserId(userId) }
    }
    
    @Test
    fun `findByEmail should return user by email when found`() {
        // Given
        val userService = mockk<UserService>()
        val usersController = UsersController(userService)
        
        val testUserDTO = mockk<UserDTO>()
        val email = "test@example.com"
        
        every { userService.findByEmail(email) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.findByEmail(email)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { userService.findByEmail(email) }
    }
    
    @Test
    fun `createOrUpdateUser should create or update user`() {
        // Given
        val userService = mockk<UserService>()
        val usersController = UsersController(userService)
        
        val testUserDTO = mockk<UserDTO>()
        every { testUserDTO.email } returns "test@example.com"
        
        every { userService.createOrUpdateUser(testUserDTO) } returns Ok(testUserDTO)
        
        // When
        val response = usersController.createOrUpdateUser(testUserDTO)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify { userService.createOrUpdateUser(testUserDTO) }
    }
} 