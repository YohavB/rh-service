package com.yb.rh.integration

import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.dtos.CarDTO
import com.yb.rh.dtos.FindCarRequestDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CarIntegrationTest : IntegrationTestBase() {

    @Test
    fun `test get car or create successfully`() {
        val findCarRequest = FindCarRequestDTO(
            plateNumber = "ABC123",
            country = Countries.IL
        )

        val response = performPost("/api/v1/car", findCarRequest)
        val carDTO = objectMapper.readValue(response, CarDTO::class.java)

        assertEquals("ABC123", carDTO.plateNumber)
        assertEquals(Countries.IL, carDTO.country)
        assertEquals(Brands.TOYOTA, carDTO.brand)
        assertEquals("Corolla", carDTO.model)
        assertEquals(Colors.WHITE, carDTO.color)

        // Verify database state
        assertEquals(1, countRowsInTable("cars"))
        val dbCar = getRowFromTable("cars", carDTO.id)
        assertNotNull(dbCar)
        assertEquals("ABC123", dbCar!!["plate_number"])
        assertEquals("IL", dbCar["country"])
    }

    @Test
    fun `test get car or create returns existing car`() {
        val findCarRequest = FindCarRequestDTO(
            plateNumber = "XYZ789",
            country = Countries.IL
        )

        // Create car first time
        val firstResponse = performPost("/api/v1/car", findCarRequest)
        val firstCar = objectMapper.readValue(firstResponse, CarDTO::class.java)

        // Try to create same car again
        val secondResponse = performPost("/api/v1/car", findCarRequest)
        val secondCar = objectMapper.readValue(secondResponse, CarDTO::class.java)

        // Should return the same car
        assertEquals(firstCar.id, secondCar.id)
        assertEquals("XYZ789", secondCar.plateNumber)

        // Verify only one car exists in database
        assertEquals(1, countRowsInTable("cars"))
    }
} 