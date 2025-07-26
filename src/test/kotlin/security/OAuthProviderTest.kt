package com.yb.rh.security

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class OAuthProviderTest {
    
    @Test
    fun `should support Google provider`() {
        val provider = OAuthProvider.GOOGLE
        assertEquals("Google", provider.displayName)
        assertEquals("🔍", provider.icon)
    }
    
    @Test
    fun `should support Apple provider`() {
        val provider = OAuthProvider.APPLE
        assertEquals("Apple", provider.displayName)
        assertEquals("🍎", provider.icon)
    }
    
    @Test
    fun `should support Facebook provider`() {
        val provider = OAuthProvider.FACEBOOK
        assertEquals("Facebook", provider.displayName)
        assertEquals("📘", provider.icon)
    }
    
    @Test
    fun `should find provider by name`() {
        assertEquals(OAuthProvider.GOOGLE, OAuthProvider.fromString("GOOGLE"))
        assertEquals(OAuthProvider.APPLE, OAuthProvider.fromString("APPLE"))
        assertEquals(OAuthProvider.FACEBOOK, OAuthProvider.fromString("FACEBOOK"))
        assertNull(OAuthProvider.fromString("TWITTER"))
    }
    
    @Test
    fun `should find provider case insensitive`() {
        assertEquals(OAuthProvider.GOOGLE, OAuthProvider.fromString("google"))
        assertEquals(OAuthProvider.APPLE, OAuthProvider.fromString("apple"))
        assertEquals(OAuthProvider.FACEBOOK, OAuthProvider.fromString("facebook"))
    }
} 