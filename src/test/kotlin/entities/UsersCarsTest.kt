package entities

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UsersCarsTest {

    private val testUser = User(
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        pushNotificationToken = "test-token",
        urlPhoto = null,
        userId = 1L
    )
    
    private val testCar = Car(
        plateNumber = "ABC123",
        country = Countries.IL,
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )
    
    private val blockingCar = Car(
        plateNumber = "BLOCKING123",
        country = Countries.IL,
        brand = Brands.BMW,
        model = "X5",
        color = Colors.WHITE,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )
    
    private val blockedCar = Car(
        plateNumber = "BLOCKED123",
        country = Countries.IL,
        brand = Brands.AUDI,
        model = "A4",
        color = Colors.RED,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    @Test
    fun `UsersCars entity should have correct properties`() {
        // Given & When
        val usersCars = UsersCars(
            user = testUser,
            car = testCar
        )
        
        // Then
        assertEquals(testUser, usersCars.user)
        assertEquals(testCar, usersCars.car)
        assertNull(usersCars.blockingCar)
        assertNull(usersCars.blockedCar)
        assertEquals(0L, usersCars.id) // Default ID value
    }
    
    @Test
    fun `blockedBy should set the blocking car`() {
        // Given
        val usersCars = UsersCars(testUser, testCar)
        
        // When
        usersCars.blockedBy(blockingCar)
        
        // Then
        assertEquals(blockingCar, usersCars.blockingCar)
    }
    
    @Test
    fun `blocking should set the blocked car`() {
        // Given
        val usersCars = UsersCars(testUser, testCar)
        
        // When
        usersCars.blocking(blockedCar)
        
        // Then
        assertEquals(blockedCar, usersCars.blockedCar)
    }
    
    @Test
    fun `unblocked should clear the blocking car`() {
        // Given
        val usersCars = UsersCars(testUser, testCar, blockingCar)
        
        // When
        usersCars.unblocked()
        
        // Then
        assertNull(usersCars.blockingCar)
    }
    
    @Test
    fun `unblocking should clear the blocked car`() {
        // Given
        val usersCars = UsersCars(testUser, testCar, null, blockedCar)
        
        // When
        usersCars.unblocking()
        
        // Then
        assertNull(usersCars.blockedCar)
    }
    
    @Test
    fun `toDto should convert entity to DTO`() {
        // Given
        val usersCars = UsersCars(
            user = testUser,
            car = testCar,
            blockingCar = blockingCar,
            blockedCar = blockedCar
        )
        
        // When
        val dto = usersCars.toDto()
        
        // Then
        assertEquals(testUser.userId, dto.userId)
        assertEquals(testCar.plateNumber, dto.userCar)
        assertEquals(blockingCar.plateNumber, dto.blockingCar)
        assertEquals(blockedCar.plateNumber, dto.blockedCar)
    }
} 