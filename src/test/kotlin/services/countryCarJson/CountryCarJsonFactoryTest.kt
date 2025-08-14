package com.yb.rh.services.countryCarJson

import com.yb.rh.enum.Countries
import com.yb.rh.utils.countryCarJson.CountryCarJsonFactory
import com.yb.rh.utils.countryCarJson.IlCarJsonHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CountryCarJsonFactoryTest {

    private lateinit var factory: CountryCarJsonFactory

    @BeforeEach
    fun setUp() {
        factory = CountryCarJsonFactory()
    }

    @Test
    fun `test getCountryCarJsonHandler for Israel returns IlCarJsonHandler`() {
        // When
        val result = factory.getCountryCarJsonHandler(Countries.IL)

        // Then
        assertNotNull(result)
        assertTrue(result is IlCarJsonHandler)
    }

    @Test
    fun `test getCountryCarJsonHandler for unsupported country throws exception`() {
        // When & Then
        assertThrows<IllegalArgumentException> {
            factory.getCountryCarJsonHandler(Countries.UNKNOWN)
        }
    }
}