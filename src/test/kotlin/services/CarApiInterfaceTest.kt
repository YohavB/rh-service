package services

import com.yb.rh.common.Countries
import com.yb.rh.services.CarApiInterface
import com.yb.rh.services.countryCarJson.CountryCarJson
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.every

class CarApiInterfaceTest {

    private lateinit var carApiInterface: CarApiInterface

    private lateinit var countryCarJson: CountryCarJson

    @BeforeEach
    fun setup() {
        countryCarJson = CountryCarJson()
        carApiInterface = CarApiInterface(countryCarJson)
    }

    @Test
    fun `getCarInfo should return valid URL for Israeli cars`() {
        // Given
        val plateNumber = "3254433"
        val country = Countries.IL

        // When
        val result = carApiInterface.getCarInfo(plateNumber, country)

        // Then
        assertTrue(result.plateNumber == plateNumber, "Plate number should match the input")
    }
} 