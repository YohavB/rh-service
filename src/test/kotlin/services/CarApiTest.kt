package com.yb.rh.services

import com.yb.rh.enum.Countries
import com.yb.rh.utils.countryCarJson.CountryCarJsonFactory
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CarApiTest {
    private lateinit var countryCarJsonFactory: CountryCarJsonFactory
    private lateinit var carApi: CarApiService

    @BeforeEach
    fun setUp() {
        countryCarJsonFactory = mockk()
        carApi = CarApiService(countryCarJsonFactory)
    }

    @Test
    fun `test getIsraelCar returns correct URL`() {
        val plateNumber = "ABC123"
        val expectedUrl = "https://data.gov.il/api/3/action/datastore_search?resource_id=053cea08-09bc-40ec-8f7a-156f0677aff3&q=$plateNumber"

        val result = carApi.getIsraelCar(plateNumber)

        assertEquals(expectedUrl, result)
    }

    @Test
    fun `test getCarByCountry with Israel`() {
        val plateNumber = "ABC123"

        val result = carApi.getCarByCountry(plateNumber, Countries.IL)

        assertTrue(result.contains("data.gov.il"))
        assertTrue(result.contains(plateNumber))
    }

    @Test
    fun `test getCarByCountry with unsupported country`() {
        val plateNumber = "ABC123"

        assertThrows<IllegalArgumentException> {
            carApi.getCarByCountry(plateNumber, Countries.UNKNOWN)
        }
    }
} 