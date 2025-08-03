package com.yb.rh.dtos

interface OAuthUserInfoDTO {
    val email: String?
    val name: String?
    val firstName: String?
    val lastName: String?
    val picture: String?

    fun toUserDTO(): UserDTO
}

data class GoogleUserInfoDTO(
    override val email: String?,
    override val name: String?,
    val givenName: String?,
    val familyName: String?,
    override val picture: String?,
) : OAuthUserInfoDTO {
    override val firstName: String? = givenName
    override val lastName: String? = familyName

    override fun toUserDTO(): UserDTO {
        return UserDTO(
            email = email ?: "",
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            urlPhoto = picture,
            id = 0L
        )
    }
}

data class FacebookUserInfoDTO(
    val id: String,
    override val name: String?,
    override val email: String?,
    val fbFirstName: String?,
    val fbLastName: String?,
    val fbPicture: FacebookPicture?
) : OAuthUserInfoDTO {
    override val firstName: String? = fbFirstName
    override val lastName: String? = fbLastName
    override val picture: String? = fbPicture?.data?.url

    override fun toUserDTO(): UserDTO {
        return UserDTO(
            email = email ?: "",
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            urlPhoto = picture,
            id = 0L
        )
    }
}

data class AppleUserInfoDTO(
    val sub: String,
    override val email: String?,
    override val name: String?,
) : OAuthUserInfoDTO {
    override val firstName: String? = name?.split(" ")?.firstOrNull()
    override val lastName: String? = name?.split(" ")?.drop(1)?.joinToString(" ")
    override val picture: String? = null // Apple doesn't provide profile pictures

    override fun toUserDTO(): UserDTO {
        return UserDTO(
            email = email ?: "",
            firstName = firstName ?: "",
            lastName = lastName ?: "",
            urlPhoto = picture,
            id = 0L
        )
    }
}

data class FacebookPicture(
    val data: FacebookPictureData?
)

data class FacebookPictureData(
    val url: String?
) 