package com.yb.rh.dtos

data class OAuthLoginRequestDTO(
    val token: String
)

data class AuthResponseDTO(
    val token: String,
    val user: UserDTO
) 