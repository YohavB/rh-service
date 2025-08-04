package com.yb.rh.enum

import com.fasterxml.jackson.annotation.JsonValue

enum class OAuthProvider(val displayName: String, val icon: String) {
    GOOGLE("Google", "🔍"),
    APPLE("Apple", "🍎"),
    FACEBOOK("Facebook", "📘");

    @JsonValue
    fun getValue(): String = name

    companion object {
        fun fromString(name: String): OAuthProvider? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }
    }
}