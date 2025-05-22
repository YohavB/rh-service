package services.countryCarJson

import com.google.gson.Gson
import com.yb.rh.common.Countries
import com.yb.rh.services.countryCarJson.CountryCarJson
import com.yb.rh.services.countryCarJson.IlCarJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Simple test for CountryCarJson that focuses on method calls without mocking
 * This is to improve coverage
 */
class CountryCarJsonSimpleTest {

    @Test
    fun `getCountryCarJson returns correct class for IL country`() {
        // Given
        val countryCarJson = CountryCarJson()
        
        // When
        val result = countryCarJson.getCountryCarJson(Countries.IL)
        
        // Then
        assertEquals(IlCarJson::class.java, result)
    }
    
    @Test
    fun `IlCarJson can parse JSON response with empty records`() {
        // Given - Empty records
        val jsonWithEmptyRecords = """
        {
            "help": "API documentation",
            "result": {
                "records": []
            },
            "success": true
        }
        """.trimIndent()
        
        // When
        val ilCarJson = Gson().fromJson(jsonWithEmptyRecords, IlCarJson::class.java)
        
        // Then
        assertNotNull(ilCarJson)
        assertNotNull(ilCarJson.result)
        assertEquals(0, ilCarJson.result.records.size)
    }
    
    @Test
    fun `IlCarJson can parse JSON response with missing fields`() {
        // Given - Missing fields
        val jsonWithMissingFields = """
        {
            "result": {
                "records": [
                    {
                        "mispar_rechev": 123456
                    }
                ]
            },
            "success": true
        }
        """.trimIndent()
        
        // When
        val ilCarJson = Gson().fromJson(jsonWithMissingFields, IlCarJson::class.java)
        
        // Then
        assertNotNull(ilCarJson)
        assertNotNull(ilCarJson.result)
        assertEquals(1, ilCarJson.result.records.size)
        assertEquals("123456", ilCarJson.result.records[0].mispar_rechev.toString())
    }
} 