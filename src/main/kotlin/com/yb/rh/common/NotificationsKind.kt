package com.yb.rh.common

enum class NotificationsKind(private val value: Int, private val prettyName: String) {
    BEEN_BLOCKED(1, "You've been blocked"),
    NEED_TO_GO(2, "You need to move, please");
}

