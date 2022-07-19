package com.yb.rh.common

enum class NotificationsKind(private val value: Int, private val prettyName: String) {
    BEEN_BLOCKED(1, "You've been blocked"),
    NEED_TO_GO(2, "You need to go");

    override fun toString(): String {
        return prettyName
    }

    companion object {
        private val mapping: MutableMap<Int, NotificationsKind> = HashMap()

        fun valueOf(value: Int): NotificationsKind {
            return mapping[value] ?: throw RuntimeException("Invalid value:$value")
        }

        init {
            for (colors in NotificationsKind.values()) {
                mapping[colors.value] = colors
            }
        }
    }

    fun toInt(): Int {
        return value
    }
}

