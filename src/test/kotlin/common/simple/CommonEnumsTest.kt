package common.simple

import com.yb.rh.common.Brands
import com.yb.rh.common.CarStatus
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.common.NotificationsKind
import com.yb.rh.common.UserStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Simple test for common enums to improve coverage
 */
class CommonEnumsTest {

    @Test
    fun `enum Brands should have expected values`() {
        // When
        val brands = Brands.values()
        
        // Then
        assertNotNull(brands)
        
        // Verify key brands exist
        assertNotNull(Brands.TESLA)
        assertNotNull(Brands.BMW)
        assertNotNull(Brands.AUDI)
        assertNotNull(Brands.UNKNOWN)
    }
    
    @Test
    fun `enum Colors should have expected values`() {
        // When
        val colors = Colors.values()
        
        // Then
        assertNotNull(colors)
        
        // Verify key colors exist
        assertNotNull(Colors.BLACK)
        assertNotNull(Colors.WHITE)
        assertNotNull(Colors.RED)
        assertNotNull(Colors.UNKNOWN)
    }
    
    @Test
    fun `enum Countries should have expected values`() {
        // When
        val countries = Countries.values()
        
        // Then
        assertNotNull(countries)
        
        // Verify IL exists
        assertNotNull(Countries.IL)
        assertEquals("IL", Countries.IL.name)
    }
    
    @Test
    fun `enum CarStatus should have expected values`() {
        // When
        val statuses = CarStatus.values()
        
        // Then
        assertNotNull(statuses)
        assertNotNull(CarStatus.BLOCKED)
        assertNotNull(CarStatus.BLOCKING)
    }
    
    @Test
    fun `enum UserStatus should have expected values`() {
        // When
        val statuses = UserStatus.values()
        
        // Then
        assertNotNull(statuses)
        assertNotNull(UserStatus.BLOCKED)
        assertNotNull(UserStatus.BLOCKING)
    }
    
    @Test
    fun `enum NotificationsKind should have expected values`() {
        // When
        val kinds = NotificationsKind.values()
        
        // Then
        assertNotNull(kinds)
        assertNotNull(NotificationsKind.NEED_TO_GO)
        assertNotNull(NotificationsKind.BEEN_BLOCKED)
    }
} 