package entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.entities.CarDTO
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserTest {

    @Test
    fun `User entity should have correct properties`() {
        // Given
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val pushToken = "test-token"
        val urlPhoto = "https://example.com/photo.jpg"
        val userId = 1L
        
        // When
        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            pushNotificationToken = pushToken,
            urlPhoto = urlPhoto,
            userId = userId
        )
        
        // Then
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertEquals(email, user.email)
        assertEquals(pushToken, user.pushNotificationToken)
        assertEquals(urlPhoto, user.urlPhoto)
        assertEquals(userId, user.userId)
        assertNotNull(user.creationTime)
        assertNotNull(user.updateTime)
    }
    
    @Test
    fun `User toDto should convert entity to DTO without cars`() {
        // Given
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "test-token",
            urlPhoto = "https://example.com/photo.jpg",
            userId = 1L
        )
        
        // When
        val dto = user.toDto()
        
        // Then
        assertEquals(user.userId, dto.id)
        assertEquals(user.firstName, dto.firstName)
        assertEquals(user.lastName, dto.lastName)
        assertEquals(user.email, dto.email)
        assertEquals(user.pushNotificationToken, dto.pushNotificationToken)
        assertEquals(user.urlPhoto, dto.urlPhoto)
        assertEquals(null, dto.userCars)
    }
    
    @Test
    fun `User toDto should convert entity to DTO with cars`() {
        // Given
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "test-token",
            urlPhoto = "https://example.com/photo.jpg",
            userId = 1L
        )
        
        val carsList = listOf(
            CarDTO(
                plateNumber = "ABC123",
                brand = Brands.TESLA,
                model = "Model 3",
                color = Colors.BLACK,
                carLicenseExpireDate = LocalDateTime.now()
            )
        )
        
        // When
        val dto = user.toDto(carsList)
        
        // Then
        assertEquals(user.userId, dto.id)
        assertEquals(user.firstName, dto.firstName)
        assertEquals(user.lastName, dto.lastName)
        assertEquals(user.email, dto.email)
        assertEquals(user.pushNotificationToken, dto.pushNotificationToken)
        assertEquals(user.urlPhoto, dto.urlPhoto)
        assertEquals(carsList, dto.userCars)
    }
    
    @Test
    fun `User fromDto should convert DTO to entity`() {
        // Given
        val dto = UserDTO(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "test-token",
            urlPhoto = "https://example.com/photo.jpg"
        )
        
        // When
        val user = User.fromDto(dto)
        
        // Then
        assertEquals(dto.firstName, user.firstName)
        assertEquals(dto.lastName, user.lastName)
        assertEquals(dto.email, user.email)
        assertEquals(dto.pushNotificationToken, user.pushNotificationToken)
        assertEquals(dto.urlPhoto, user.urlPhoto)
        // Note: userId is not transferred from DTO to entity - it's generated
    }
    
    @Test
    fun `UserDTO toEntity should convert to entity`() {
        // Given
        val dto = UserDTO(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            pushNotificationToken = "test-token",
            urlPhoto = "https://example.com/photo.jpg"
        )
        
        // When
        val user = dto.toEntity()
        
        // Then
        assertEquals(dto.firstName, user.firstName)
        assertEquals(dto.lastName, user.lastName)
        assertEquals(dto.email, user.email)
        assertEquals(dto.pushNotificationToken, user.pushNotificationToken)
        assertEquals(dto.urlPhoto, user.urlPhoto)
    }
} 