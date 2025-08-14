package com.yb.rh.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class UserIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test deactivate user successfully`() {
        // First create a user directly in database
        val userId = createUserInDatabase(
            email = "deactivate@example.com",
            firstName = "Deactivate",
            lastName = "User",
            pushNotificationToken = "deactivate-token"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Then deactivate the current user
        performPut("/api/v1/user/deactivate")

        // Verify user is deactivated in database
        val dbUser = getRowFromTable("users", userId)
        assertNotNull(dbUser)
        assertEquals(false, dbUser!!["is_active"])
    }

    @Test
    fun `test get user successfully`() {
        // First create a user directly in database
        val userId = createUserInDatabase(
            email = "getuser@example.com",
            firstName = "Get",
            lastName = "User",
            pushNotificationToken = "getuser-token"
        )

        // Setup current user for authentication
        setupCurrentUser(userId)

        // Get current user info
        val userResponse = performGet("/api/v1/user")
        val userDTO = objectMapper.readValue(userResponse, com.yb.rh.dtos.UserDTO::class.java)

        // Verify user data
        assertNotNull(userDTO)
        assertEquals(userId, userDTO.id)
        assertEquals("getuser@example.com", userDTO.email)
        assertEquals("Get", userDTO.firstName)
        assertEquals("User", userDTO.lastName)
    }
} 