package entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CarTest {

    @Test
    fun `Car entity should have correct properties`() {
        // Given
        val plateNumber = "ABC123"
        val country = Countries.IL
        val brand = Brands.TESLA
        val model = "Model 3"
        val color = Colors.BLACK
        val expireDate = LocalDateTime.now().plusYears(1)
        
        // When
        val car = Car(
            plateNumber = plateNumber,
            country = country,
            brand = brand,
            model = model,
            color = color,
            carLicenseExpireDate = expireDate
        )
        
        // Then
        assertEquals(plateNumber, car.plateNumber)
        assertEquals(brand, car.brand)
        assertEquals(model, car.model)
        assertEquals(color, car.color)
        assertEquals(expireDate, car.carLicenseExpireDate)
        assertFalse(car.isBlocked)
        assertFalse(car.isBlocking)
    }
    
    @Test
    fun `Car entity state methods should work correctly`() {
        // Given
        val car = Car(
            plateNumber = "ABC123",
            country = Countries.IL,
            brand = Brands.TESLA,
            model = "Model 3",
            color = Colors.BLACK
        )
        
        // When & Then - Test blocking state
        assertFalse(car.isBlocking)
        car.beingBlocking()
        assertTrue(car.isBlocking)
        car.unblocking()
        assertFalse(car.isBlocking)
        
        // When & Then - Test blocked state
        assertFalse(car.isBlocked)
        car.beingBlocked()
        assertTrue(car.isBlocked)
        car.unblocked()
        assertFalse(car.isBlocked)
    }
    
    @Test
    fun `Car toDto should convert entity to DTO`() {
        // Given
        val plateNumber = "ABC123"
        val country = Countries.IL
        val brand = Brands.TESLA
        val model = "Model 3"
        val color = Colors.BLACK
        val expireDate = LocalDateTime.now().plusYears(1)
        
        val car = Car(
            plateNumber = plateNumber,
            country = country,
            brand = brand,
            model = model,
            color = color,
            carLicenseExpireDate = expireDate,
            isBlocked = true,
            isBlocking = true
        )
        
        // When
        val dto = car.toDto()
        
        // Then
        assertEquals(plateNumber, dto.plateNumber)
        assertEquals(brand, dto.brand)
        assertEquals(model, dto.model)
        assertEquals(color, dto.color)
        assertEquals(expireDate, dto.carLicenseExpireDate)
        assertTrue(dto.isBlocked)
        assertTrue(dto.isBlocking)
    }
    
    @Test
    fun `Car fromDto should convert DTO to entity`() {
        // Given
        val plateNumber = "ABC123"
        val brand = Brands.TESLA
        val model = "Model 3"
        val color = Colors.BLACK
        val expireDate = LocalDateTime.now().plusYears(1)
        
        val dto = CarDTO(
            plateNumber = plateNumber,
            country = Countries.IL,
            brand = brand,
            model = model,
            color = color,
            carLicenseExpireDate = expireDate,
            isBlocked = true,
            isBlocking = true
        )
        
        // When
        val car = Car.fromDto(dto)
        
        // Then
        assertEquals(plateNumber, car.plateNumber)
        assertEquals(brand, car.brand)
        assertEquals(model, car.model)
        assertEquals(color, car.color)
        assertEquals(expireDate, car.carLicenseExpireDate)
        assertTrue(car.isBlocked)
        assertTrue(car.isBlocking)
    }
    
    @Test
    fun `CarDTO returnTest should return test instance`() {
        // When
        val dto = CarDTO.returnTest()
        
        // Then
        assertEquals("Test", dto.plateNumber)
        assertEquals(Brands.UNKNOWN, dto.brand)
        assertEquals("Test", dto.model)
        assertEquals(Colors.UNKNOWN, dto.color)
        assertNotNull(dto.carLicenseExpireDate)
    }
    
    @Test
    fun `CarDTO toEntity should convert to entity`() {
        // Given
        val dto = CarDTO(
            plateNumber = "ABC123",
            country = Countries.IL,
            brand = Brands.TESLA,
            model = "Model 3",
            color = Colors.BLACK,
            carLicenseExpireDate = LocalDateTime.now()
        )
        
        // When
        val car = dto.toEntity()
        
        // Then
        assertEquals(dto.plateNumber, car.plateNumber)
        assertEquals(dto.brand, car.brand)
        assertEquals(dto.model, car.model)
        assertEquals(dto.color, car.color)
        assertEquals(dto.carLicenseExpireDate, car.carLicenseExpireDate)
    }
} 