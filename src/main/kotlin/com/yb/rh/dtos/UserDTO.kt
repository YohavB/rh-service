package com.yb.rh.dtos

import com.yb.rh.entities.User

data class UserDTO(
    val id: Long,
    var firstName: String,
    var lastName: String,
    var email: String,
    var urlPhoto: String? = null,
)

data class UserCreationDTO(
    var firstName: String,
    var lastName: String,
    var email: String,
    var pushNotificationToken: String,
    var urlPhoto: String? = null,
) {
    fun toEntity() = User.fromDto(this)
}