package com.yb.rh.utils

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ExtensionsTest {

    @Test
    fun `test format LocalDateTime`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45)

        // When
        val formatted = dateTime.format()

        // Then
        assertEquals("2023-12-25 25th 2023", formatted)
    }

    @Test
    fun `test toSlug with simple string`() {
        // Given
        val input = "Hello World"

        // When
        val result = input.toSlug()

        // Then
        assertEquals("hello-world", result)
    }

    @Test
    fun `test toSlug with special characters`() {
        // Given
        val input = "Hello, World! @#$%"

        // When
        val result = input.toSlug()

        // Then
        assertEquals("hello-world", result)
    }

    @Test
    fun `test toSlug with multiple spaces`() {
        // Given
        val input = "Hello   World"

        // When
        val result = input.toSlug()

        // Then
        assertEquals("hello-world", result)
    }

    @Test
    fun `test toSlug with newlines`() {
        // Given
        val input = "Hello\nWorld"

        // When
        val result = input.toSlug()

        // Then
        assertEquals("hello-world", result)
    }

    @Test
    fun `test toSlug with trailing hyphens`() {
        // Given
        val input = "Hello World---"

        // When
        val result = input.toSlug()

        // Then
        assertEquals("hello-world", result)
    }

    @Test
    fun `test maskPII with long string`() {
        // Given
        val input = "123456789"

        // When
        val result = input.maskPII()

        // Then
        assertEquals("1*9", result)
    }

    @Test
    fun `test maskPII with short string`() {
        // Given
        val input = "123"

        // When
        val result = input.maskPII()

        // Then
        assertEquals("*", result)
    }

    @Test
    fun `test maskPII with very short string`() {
        // Given
        val input = "12"

        // When
        val result = input.maskPII()

        // Then
        assertEquals("*", result)
    }

    @Test
    fun `test maskPII with single character`() {
        // Given
        val input = "1"

        // When
        val result = input.maskPII()

        // Then
        assertEquals("*", result)
    }

    @Test
    fun `test maskEmail with valid email`() {
        // Given
        val input = "john.doe@example.com"

        // When
        val result = input.maskEmail()

        // Then
        assertEquals("***doe***", result)
    }

    @Test
    fun `test maskEmail with short name`() {
        // Given
        val input = "j@example.com"

        // When
        val result = input.maskEmail()

        // Then
        assertEquals("j***", result)
    }

    @Test
    fun `test maskEmail with two character name`() {
        // Given
        val input = "jo@example.com"

        // When
        val result = input.maskEmail()

        // Then
        assertEquals("jo***", result)
    }

    @Test
    fun `test maskEmail with no @ symbol`() {
        // Given
        val input = "invalidemail"

        // When
        val result = input.maskEmail()

        // Then
        assertEquals("***", result)
    }

    @Test
    fun `test maskEmail with @ at beginning`() {
        // Given
        val input = "@example.com"

        // When
        val result = input.maskEmail()

        // Then
        assertEquals("***", result)
    }
} 