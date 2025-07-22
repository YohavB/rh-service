package com.yb.rh.dtos

import com.yb.rh.entities.User
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UserDTO(
    @NotNull
    val id: Long,
    @NotBlank(message = "First name is required")
    var firstName: String,
    @NotBlank(message = "Last name is required")
    var lastName: String,
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    var email: String,
    var urlPhoto: String? = null,
)

data class UserCreationDTO(
    @NotBlank(message = "First name is required")
    var firstName: String,
    @NotBlank(message = "Last name is required")
    var lastName: String,
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    var email: String,
    @NotBlank(message = "Push notification token is required")
    var pushNotificationToken: String,
    var urlPhoto: String? = null,
) {
    fun toEntity() = User.fromDto(this)
}