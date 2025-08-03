package com.yb.rh.enum

enum class NotificationsKind(private val value: Int, val notificationTitle: String, val notificationMessage: String) {
    BEEN_BLOCKED(1, "You've been blocked", "Don't forget to use the 'Let Me Go' button when you'll leave"),
    BEEN_BLOCKING(2, "You are blocking a car", "Prepare yourself to move your car if needed"),
    NEED_TO_GO(3, "Please move your car", "The car you are blocking needs to leave"),
    FREE_TO_GO(4, "You are free to go", "No car is blocking you anymore"),
}

