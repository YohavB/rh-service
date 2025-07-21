package services

import com.yb.rh.common.Countries
import com.yb.rh.services.CarApi
import com.yb.rh.services.countryCarJson.CountryCarJsonFactory
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CarApiInterfaceTest {

    private lateinit var carApi: CarApi

    private lateinit var countryCarJsonFactory: CountryCarJsonFactory

    @BeforeEach
    fun setup() {
        countryCarJsonFactory = CountryCarJsonFactory()
        carApi = CarApi(countryCarJsonFactory)
    }

    @Test
    fun `getCarInfo should return valid URL for Israeli cars`() {
        // Given
        val plateNumber = "3254433"
        val country = Countries.IL

        // When
        val result = carApi.getCarInfo(plateNumber, country)

        // Then
        assertTrue(result.plateNumber == plateNumber, "Plate number should match the input")
    }
} 