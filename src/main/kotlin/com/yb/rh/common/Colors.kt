package com.yb.rh.common

enum class Colors(private val value: Int, private val prettyName: String) {
    WHITE(0, "White"),
    BLACK(1, "Black");

    override fun toString(): String {
        return prettyName
    }

    companion object {
        private val mapping: MutableMap<Int, Colors> = HashMap()

        fun valueOf(value: Int): Colors {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (colors in Colors.values()) {
                mapping[colors.value] = colors
            }
        }
    }

    fun toInt(): Int {
        return value
    }
}

