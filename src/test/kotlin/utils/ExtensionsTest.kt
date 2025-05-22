package utils

import com.yb.rh.utils.format
import com.yb.rh.utils.toSlug
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ExtensionsTest {

    @Test
    fun `format converts LocalDateTime to formatted string`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 5, 10, 14, 30)
        
        // When
        val result = dateTime.format()
        
        // Then
        assertEquals("2023-05-10 10th 2023", result)
    }
    
    @Test
    fun `format handles different days with correct ordinals`() {
        // Test different ordinals (1st, 2nd, 3rd, etc.)
        
        // 1st
        val firstDay = LocalDateTime.of(2023, 5, 1, 14, 30)
        assertEquals("2023-05-01 1st 2023", firstDay.format())
        
        // 2nd
        val secondDay = LocalDateTime.of(2023, 5, 2, 14, 30)
        assertEquals("2023-05-02 2nd 2023", secondDay.format())
        
        // 3rd
        val thirdDay = LocalDateTime.of(2023, 5, 3, 14, 30)
        assertEquals("2023-05-03 3rd 2023", thirdDay.format())
        
        // 4th
        val fourthDay = LocalDateTime.of(2023, 5, 4, 14, 30)
        assertEquals("2023-05-04 4th 2023", fourthDay.format())
        
        // 11th (special case)
        val eleventhDay = LocalDateTime.of(2023, 5, 11, 14, 30)
        assertEquals("2023-05-11 11th 2023", eleventhDay.format())
        
        // 12th (special case)
        val twelfthDay = LocalDateTime.of(2023, 5, 12, 14, 30)
        assertEquals("2023-05-12 12th 2023", twelfthDay.format())
        
        // 21st
        val twentyFirstDay = LocalDateTime.of(2023, 5, 21, 14, 30)
        assertEquals("2023-05-21 21st 2023", twentyFirstDay.format())
    }
    
    @Test
    fun `toSlug converts string to URL-friendly slug`() {
        // Simple test
        assertEquals("hello-world", "Hello World".toSlug())
        
        // With special characters
        assertEquals("hello-world", "Hello, World!".toSlug())
        
        // With multiple spaces
        assertEquals("hello-world", "Hello   World".toSlug())
        
        // With numbers
        assertEquals("hello-world-123", "Hello World 123".toSlug())
        
        // With newlines
        assertEquals("hello-world", "Hello\nWorld".toSlug())
        
        // Test trailing hyphen is removed
        assertEquals("hello-world", "Hello World-".toSlug())
        
        // Complex example
        assertEquals("this-is-a-test-12345", "This is a test! 12345".toSlug())
    }
} 