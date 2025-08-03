package com.yb.rh.enum

enum class OAuthProvider(val displayName: String, val icon: String) {
    GOOGLE("Google", "🔍"),
    APPLE("Apple", "🍎"),
    FACEBOOK("Facebook", "📘");

    companion object {
        fun fromString(name: String): OAuthProvider? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }
    }
}