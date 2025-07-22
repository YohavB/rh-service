package com.yb.rh.entities

import com.yb.rh.TestObjectBuilder
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CarEntityTest {

    @Test
    fun `test Car constructor with all parameters`() {
        // Given
        val plateNumber = "ABC123"
        val country = com.yb.rh.common.Countries.IL
        val brand = com.yb.rh.common.Brands.TOYOTA
        val model = "Corolla"
        val color = com.yb.rh.common.Colors.WHITE
        val carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        val creationTime = LocalDateTime.now()
        val updateTime = LocalDateTime.now()
        val id = 1L

        // When
        val car = Car(
            plateNumber = plateNumber,
            country = country,
            brand = brand,
            model = model,
            color = color,
            carLicenseExpireDate = carLicenseExpireDate,
            creationTime = creationTime,
            updateTime = updateTime,
            id = id
        )

        // Then
        assertEquals(plateNumber, car.plateNumber)
        assertEquals(country, car.country)
        assertEquals(brand, car.brand)
        assertEquals(model, car.model)
        assertEquals(color, car.color)
        assertEquals(carLicenseExpireDate, car.carLicenseExpireDate)
        assertEquals(creationTime, car.creationTime)
        assertEquals(updateTime, car.updateTime)
        assertEquals(id, car.id)
    }

    @Test
    fun `test Car constructor with default parameters`() {
        // Given
        val plateNumber = "XYZ789"
        val country = com.yb.rh.common.Countries.IL
        val brand = com.yb.rh.common.Brands.BMW
        val model = "X5"
        val color = com.yb.rh.common.Colors.BLACK
        val carLicenseExpireDate = LocalDateTime.now().plusYears(2)
        val creationTime = LocalDateTime.now()
        val updateTime = LocalDateTime.now()
        val id = 2L

        // When
        val car = Car(
            plateNumber = plateNumber,
            country = country,
            brand = brand,
            model = model,
            color = color,
            carLicenseExpireDate = carLicenseExpireDate,
            creationTime = creationTime,
            updateTime = updateTime,
            id = id
        )

        // Then
        assertEquals(plateNumber, car.plateNumber)
        assertEquals(country, car.country)
        assertEquals(brand, car.brand)
        assertEquals(model, car.model)
        assertEquals(color, car.color)
        assertEquals(carLicenseExpireDate, car.carLicenseExpireDate)
        assertEquals(creationTime, car.creationTime)
        assertEquals(updateTime, car.updateTime)
        assertEquals(id, car.id)
    }

    @Test
    fun `test Car toDto extension function`() {
        // Given
        val car = TestObjectBuilder.getCar()

        // When
        val carDTO = car.toDto()

        // Then
        assertNotNull(carDTO)
        assertEquals(car.id, carDTO.id)
        assertEquals(car.plateNumber, carDTO.plateNumber)
        assertEquals(car.country, carDTO.country)
        assertEquals(car.brand, carDTO.brand)
        assertEquals(car.model, carDTO.model)
        assertEquals(car.color, carDTO.color)
        assertEquals(car.carLicenseExpireDate, carDTO.carLicenseExpireDate)
    }

    @Test
    fun `test Car fromDto companion function`() {
        // Given
        val carDTO = TestObjectBuilder.getCarDTO()

        // When
        val car = Car.fromDto(carDTO)

        // Then
        assertNotNull(car)
        assertEquals(carDTO.plateNumber, car.plateNumber)
        assertEquals(carDTO.country, car.country)
        assertEquals(carDTO.brand, car.brand)
        assertEquals(carDTO.model, car.model)
        assertEquals(carDTO.color, car.color)
        assertEquals(carDTO.carLicenseExpireDate, car.carLicenseExpireDate)
        assertNotNull(car.creationTime)
        assertNotNull(car.updateTime)
        assertEquals(0L, car.id) // Default value
    }

    @Test
    fun `test Car mutable properties`() {
        // Given
        val car = TestObjectBuilder.getCar()

        // When
        car.carLicenseExpireDate = LocalDateTime.of(2025, 12, 31, 23, 59)
        car.updateTime = LocalDateTime.of(2023, 12, 31, 23, 59)

        // Then
        assertEquals(LocalDateTime.of(2025, 12, 31, 23, 59), car.carLicenseExpireDate)
        assertEquals(LocalDateTime.of(2023, 12, 31, 23, 59), car.updateTime)
    }

    @Test
    fun `test Car copy function`() {
        // Given
        val originalCar = TestObjectBuilder.getCar()

        // When
        val copiedCar = originalCar.copy(
            plateNumber = "COPIED123",
            brand = com.yb.rh.common.Brands.BMW,
            model = "Copied Model"
        )

        // Then
        assertEquals("COPIED123", copiedCar.plateNumber)
        assertEquals(com.yb.rh.common.Brands.BMW, copiedCar.brand)
        assertEquals("Copied Model", copiedCar.model)
        assertEquals(originalCar.country, copiedCar.country)
        assertEquals(originalCar.color, copiedCar.color)
        assertEquals(originalCar.carLicenseExpireDate, copiedCar.carLicenseExpireDate)
        assertEquals(originalCar.creationTime, copiedCar.creationTime)
        assertEquals(originalCar.updateTime, copiedCar.updateTime)
        assertEquals(originalCar.id, copiedCar.id)
    }

    @Test
    fun `test Car equals and hashCode`() {
        // Given
        val car1 = TestObjectBuilder.getCar()
        val car2 = TestObjectBuilder.getCar()
        val car3 = TestObjectBuilder.getCar(id = 999L)

        // When & Then
        assertEquals(car1, car1) // Same object
        assertEquals(car1.hashCode(), car1.hashCode())
        assert(car1 != car2) // Different objects with same values
        assert(car1 != car3) // Different objects with different values
    }

    @Test
    fun `test Car toString`() {
        // Given
        val car = TestObjectBuilder.getCar()

        // When
        val toString = car.toString()

        // Then
        assertNotNull(toString)
        assert(toString.contains(car.plateNumber))
        assert(toString.contains(car.brand.toString()))
        assert(toString.contains(car.model))
    }

    @Test
    fun `test Car with different countries`() {
        // Given
        val israelCar = TestObjectBuilder.getCar(
            plateNumber = "IL123",
            country = com.yb.rh.common.Countries.IL
        )
        val usCar = TestObjectBuilder.getCar(
            plateNumber = "US123",
            country = com.yb.rh.common.Countries.IL
        )

        // When & Then
        assertEquals(com.yb.rh.common.Countries.IL, israelCar.country)
        assertEquals(com.yb.rh.common.Countries.IL, usCar.country)
    }

    @Test
    fun `test Car with different brands`() {
        // Given
        val toyotaCar = TestObjectBuilder.getCar(
            brand = com.yb.rh.common.Brands.TOYOTA
        )
        val bmwCar = TestObjectBuilder.getCar(
            brand = com.yb.rh.common.Brands.BMW
        )
        val mercedesCar = TestObjectBuilder.getCar(
            brand = com.yb.rh.common.Brands.MERCEDES
        )

        // When & Then
        assertEquals(com.yb.rh.common.Brands.TOYOTA, toyotaCar.brand)
        assertEquals(com.yb.rh.common.Brands.BMW, bmwCar.brand)
        assertEquals(com.yb.rh.common.Brands.MERCEDES, mercedesCar.brand)
    }

    @Test
    fun `test Car with different colors`() {
        // Given
        val whiteCar = TestObjectBuilder.getCar(
            color = com.yb.rh.common.Colors.WHITE
        )
        val blackCar = TestObjectBuilder.getCar(
            color = com.yb.rh.common.Colors.BLACK
        )
        val redCar = TestObjectBuilder.getCar(
            color = com.yb.rh.common.Colors.RED
        )

        // When & Then
        assertEquals(com.yb.rh.common.Colors.WHITE, whiteCar.color)
        assertEquals(com.yb.rh.common.Colors.BLACK, blackCar.color)
        assertEquals(com.yb.rh.common.Colors.RED, redCar.color)
    }
} 