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

        // Then deactivate the user
        performPut("/api/v1/user/deactivate/$userId")

        // Verify user is deactivated in database
        val dbUser = getRowFromTable("users", userId)
        assertNotNull(dbUser)
        assertEquals(false, dbUser!!["is_active"])
    }

    @Test
    fun `test activate user successfully`() {
        // First create a user directly in database
        val userId = createUserInDatabase(
            email = "activate@example.com",
            firstName = "Activate",
            lastName = "User",
            pushNotificationToken = "activate-token"
        )

        // Deactivate the user first
        performPut("/api/v1/user/deactivate/$userId")

        // Then activate the user
        performPut("/api/v1/user/activate/$userId")

        // Verify user is activated in database
        val dbUser = getRowFromTable("users", userId)
        assertNotNull(dbUser)
        assertEquals(true, dbUser!!["is_active"])
    }
} 