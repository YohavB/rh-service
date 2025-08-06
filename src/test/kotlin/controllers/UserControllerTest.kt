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
    fun `test getUser success`() {
        // Given
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { userService.getUserDTOByToken() } returns userDTO

        // When
        val result = userController.getUser()

        // Then
        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        assertEquals(userDTO.firstName, result.firstName)
        assertEquals(userDTO.lastName, result.lastName)
        assertEquals(userDTO.email, result.email)
        verify { userService.getUserDTOByToken() }
    }

    @Test
    fun `test deactivateUserByPath success`() {
        // Given
        every { userService.deActivateUser() } returns Unit

        // When
        userController.deactivateUserByPath()

        // Then
        verify { userService.deActivateUser() }
    }

    @Test
    fun `test activateUserByPath success`() {
        // Given
        every { userService.activateUser() } returns Unit

        // When
        userController.activateUserByPath()

        // Then
        verify { userService.activateUser() }
    }
} 