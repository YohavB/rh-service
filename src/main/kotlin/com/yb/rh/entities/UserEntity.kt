package com.yb.rh.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    var firstName: String,
    var lastName: String,
    @Column(unique = true)
    @NotNull
    var email: String,
    @Column(unique = true)
    @NotNull
    var pushNotificationToken: String,
    var urlPhoto: String?,

    @CreationTimestamp
    @Column(name = "creation_time")
    var creationTime: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0,
) {
    fun toDto(usersCars: List<CarDTO>? = null) =
        UserDTO(userId, firstName, lastName, email, pushNotificationToken, urlPhoto, usersCars)

    companion object {
        fun fromDto(userDTO: UserDTO) =
            User(
                userDTO.firstName,
                userDTO.lastName,
                userDTO.email,
                userDTO.pushNotificationToken,
                userDTO.urlPhoto
            )
    }
}

data class UserDTO(
    val id: Long,
    var firstName: String,
    var lastName: String,
    var email: String,
    var pushNotificationToken: String,
    var urlPhoto: String?,
    val userCars: List<CarDTO>? = null,
) {
    fun toEntity() = User.fromDto(this)
}