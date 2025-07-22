package com.yb.rh.controllers

import com.yb.rh.TestObjectBuilder
import com.yb.rh.services.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserControllerTest {
    private lateinit var userService: UserService
    private lateinit var userController: UsersController

    @BeforeEach
    fun setUp() {
        userService = mockk()
        userController = UsersController(userService)
    }

    @Test
    fun `test createUser success`() {
        // Given
        val userCreationDTO = TestObjectBuilder.getUserCreationDTO()
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { userService.createUser(userCreationDTO) } returns userDTO

        // When
        val result = userController.createUser(userCreationDTO)

        // Then
        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        assertEquals(userDTO.firstName, result.firstName)
        assertEquals(userDTO.lastName, result.lastName)
        assertEquals(userDTO.email, result.email)
        verify { userService.createUser(userCreationDTO) }
    }

    @Test
    fun `test getUserById success`() {
        // Given
        val userId = 1L
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { userService.getUserDTOByUserId(userId) } returns userDTO

        // When
        val result = userController.getUserById(userId)

        // Then
        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        verify { userService.getUserDTOByUserId(userId) }
    }

    @Test
    fun `test getUserByEmail success`() {
        // Given
        val email = "test@example.com"
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { userService.findUserDTOByEmail(email) } returns userDTO

        // When
        val result = userController.getUserByEmail(email)

        // Then
        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        assertEquals(userDTO.email, result.email)
        verify { userService.findUserDTOByEmail(email) }
    }

    @Test
    fun `test updateUser success`() {
        // Given
        val userDTO = TestObjectBuilder.getUserDTO()
        val updatedUserDTO = TestObjectBuilder.getUserDTO(
            firstName = "Updated",
            lastName = "Name"
        )
        
        every { userService.updateUser(userDTO) } returns updatedUserDTO

        // When
        val result = userController.updateUser(userDTO)

        // Then
        assertNotNull(result)
        assertEquals(updatedUserDTO.firstName, result.firstName)
        assertEquals(updatedUserDTO.lastName, result.lastName)
        verify { userService.updateUser(userDTO) }
    }

    @Test
    fun `test deactivateUser success`() {
        // Given
        val userId = 1L
        
        every { userService.deActivateUser(userId) } returns Unit

        // When
        userController.deactivateUser(userId)

        // Then
        verify { userService.deActivateUser(userId) }
    }

    @Test
    fun `test activateUser success`() {
        // Given
        val userId = 1L
        
        every { userService.activateUser(userId) } returns Unit

        // When
        userController.activateUser(userId)

        // Then
        verify { userService.activateUser(userId) }
    }
} 