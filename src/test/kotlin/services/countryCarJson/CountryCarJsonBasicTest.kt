package services.countryCarJson

import com.yb.rh.common.Countries
import com.yb.rh.services.countryCarJson.CountryCarJson
import com.yb.rh.services.countryCarJson.IlCarJsonHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CountryCarJsonBasicTest {

    @Test
    fun `getCountryCarJson returns correct class for IL country`() {
        // Create an instance of CountryCarJson
        val countryCarJson = CountryCarJson()
        
        // Call the method with IL country
        val result = countryCarJson.getCountryCarJsonHandler(Countries.IL)
        
        // Verify the result is an instance of IlCarJsonHandler class
        assertTrue(result is IlCarJsonHandler)
    }
} 