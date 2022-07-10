package com.yb.rh.common

enum class Brands(private val value: Int, private val prettyName: String) {
    TESLA(0, "Tesla"),
    AUDI(1, "Audi");

    override fun toString(): String {
        return prettyName
    }

    companion object {
        private val mapping: MutableMap<Int, Brands> = HashMap()

        fun valueOf(value: Int): Brands {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (brands in Brands.values()) {
                mapping[brands.value] = brands
            }
        }
    }

    fun toInt(): Int {
        return value
    }
}