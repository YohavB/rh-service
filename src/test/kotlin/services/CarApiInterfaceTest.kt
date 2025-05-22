package services

import com.yb.rh.common.Countries
import com.yb.rh.services.CarApiInterface
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CarApiInterfaceTest {

    private lateinit var carApiInterface: CarApiInterface

    @BeforeEach
    fun setup() {
        carApiInterface = CarApiInterface()
    }

    @Test
    fun `getCarInfo should return valid URL for Israeli cars`() {
        // Given
        val plateNumber = "123456"
        val country = Countries.IL
        
        // When
        val result = carApiInterface.getCarInfo(plateNumber, country)
        
        // Then
        assertTrue(result.contains("data.gov.il"))
        assertTrue(result.contains(plateNumber))
    }
} 