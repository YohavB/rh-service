package com.yb.rh.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthLoginRequestDTO(
    val token: String
)

data class AuthResponseDTO(
    val token: String,
    val user: UserDTO
) 