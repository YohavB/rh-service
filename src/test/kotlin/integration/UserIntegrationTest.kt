package com.yb.rh.integration

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.UserDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class UserIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test create user successfully`() {
        val userCreationDTO = UserCreationDTO(
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            pushNotificationToken = "test-token-123"
        )

        val response = performPost("/api/v1/user", userCreationDTO)
        val userDTO = objectMapper.readValue(response, UserDTO::class.java)

        assertEquals("test@example.com", userDTO.email)
        assertEquals("John", userDTO.firstName)
        assertEquals("Doe", userDTO.lastName)

        // Verify database state
        assertEquals(1, countRowsInTable("users"))
        val dbUser = getRowFromTable("users", userDTO.id)
        assertNotNull(dbUser)
        assertEquals("test@example.com", dbUser!!["email"])
    }

    @Test
    fun `test get user by id successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "get@example.com",
            firstName = "Jane",
            lastName = "Smith",
            pushNotificationToken = "get-token-456"
        )

        val createResponse = performPost("/api/v1/user", userCreationDTO)
        val createdUser = objectMapper.readValue(createResponse, UserDTO::class.java)

        // Then get the user by ID
        val getResponse = performGet("/api/v1/user?id=${createdUser.id}")
        val retrievedUser = objectMapper.readValue(getResponse, UserDTO::class.java)

        assertEquals(createdUser.id, retrievedUser.id)
        assertEquals("get@example.com", retrievedUser.email)
        assertEquals("Jane", retrievedUser.firstName)
        assertEquals("Smith", retrievedUser.lastName)
    }

    @Test
    fun `test get user by email successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "email@example.com",
            firstName = "Bob",
            lastName = "Johnson",
            pushNotificationToken = "email-token-789"
        )

        performPost("/api/v1/user", userCreationDTO)

        // Then get the user by email
        val getResponse = performGet("/api/v1/user/by-email?email=email@example.com")
        val retrievedUser = objectMapper.readValue(getResponse, UserDTO::class.java)

        assertEquals("email@example.com", retrievedUser.email)
        assertEquals("Bob", retrievedUser.firstName)
        assertEquals("Johnson", retrievedUser.lastName)
    }

    @Test
    fun `test update user successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "update@example.com",
            firstName = "Original",
            lastName = "Name",
            pushNotificationToken = "original-token"
        )

        val createResponse = performPost("/api/v1/user", userCreationDTO)
        val createdUser = objectMapper.readValue(createResponse, UserDTO::class.java)

        // Then update the user
        val updateDTO = UserDTO(
            id = createdUser.id,
            email = "updated@example.com",
            firstName = "Updated",
            lastName = "Name"
        )

        val updateResponse = performPut("/api/v1/user", updateDTO)
        val updatedUser = objectMapper.readValue(updateResponse, UserDTO::class.java)

        assertEquals("updated@example.com", updatedUser.email)
        assertEquals("Updated", updatedUser.firstName)

        // Verify database state
        val dbUser = getRowFromTable("users", createdUser.id)
        assertNotNull(dbUser)
        assertEquals("updated@example.com", dbUser!!["email"])
    }

    @Test
    fun `test deactivate user successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "deactivate@example.com",
            firstName = "Deactivate",
            lastName = "User",
            pushNotificationToken = "deactivate-token"
        )

        val createResponse = performPost("/api/v1/user", userCreationDTO)
        val createdUser = objectMapper.readValue(createResponse, UserDTO::class.java)

        // Then deactivate the user
        performPut("/api/v1/user/deactivate?id=${createdUser.id}")

        // Verify user is deactivated in database
        val dbUser = getRowFromTable("users", createdUser.id)
        assertNotNull(dbUser)
        assertEquals(false, dbUser!!["is_active"])
    }

    @Test
    fun `test activate user successfully`() {
        // First create a user
        val userCreationDTO = UserCreationDTO(
            email = "activate@example.com",
            firstName = "Activate",
            lastName = "User",
            pushNotificationToken = "activate-token"
        )

        val createResponse = performPost("/api/v1/user", userCreationDTO)
        val createdUser = objectMapper.readValue(createResponse, UserDTO::class.java)

        // Deactivate the user first
        performPut("/api/v1/user/deactivate?id=${createdUser.id}")

        // Then activate the user
        performPut("/api/v1/user/activate?id=${createdUser.id}")

        // Verify user is activated in database
        val dbUser = getRowFromTable("users", createdUser.id)
        assertNotNull(dbUser)
        assertEquals(true, dbUser!!["is_active"])
    }
} 