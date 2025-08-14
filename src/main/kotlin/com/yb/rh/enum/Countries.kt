package com.yb.rh.enum

import com.fasterxml.jackson.annotation.JsonValue

enum class Countries(private val value: Int, private val prettyName: String) {
    UNKNOWN(0, "Unknown"),
    IL(1, "Israel");

    @JsonValue
    fun getPrettyName(): String = prettyName
}

