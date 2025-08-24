package com.yb.rh.enum

import com.fasterxml.jackson.annotation.JsonValue

enum class NotificationsKind(private val value: Int, val notificationTitle: String, val notificationMessage: String, val sound: String) {
    BEEN_BLOCKED(1, "You've been blocked", "Don't forget to use the 'Let Me Go' button when you'll leave", NotificationsSound.DEFAULT.soundFileName),
    BEEN_BLOCKING(2, "You are blocking a car", "Prepare yourself to move your car if needed", NotificationsSound.DEFAULT.soundFileName),
    NEED_TO_GO(3, "Please move your car", "The car you are blocking needs to leave", NotificationsSound.IMPORTANT.soundFileName),
    FREE_TO_GO(4, "You are free to go", "No car is blocking you anymore", NotificationsSound.DEFAULT.soundFileName);

    @JsonValue
    fun getValue(): Int = value
}

enum class NotificationsSound(val soundFileName: String) {
    DEFAULT("car_horn.wav"),
    IMPORTANT("car_double_horn.wav"),
}

