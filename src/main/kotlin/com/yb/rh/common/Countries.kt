package com.yb.rh.common

enum class Countries(private val value: Int, private val prettyName: String) {
    IL(1, "Israel");

    override fun toString(): String {
        return prettyName
    }

    companion object {
        private val mapping: MutableMap<Int, Countries> = HashMap()

        fun valueOf(value: Int): Countries {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (colors in Countries.values()) {
                mapping[colors.value] = colors
            }
        }
    }

    fun toInt(): Int {
        return value
    }
}

