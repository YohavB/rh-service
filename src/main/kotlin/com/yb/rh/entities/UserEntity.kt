package com.yb.rh.entities

import com.yb.rh.dtos.UserCreationDTO
import com.yb.rh.dtos.UserDTO
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

    var urlPhoto: String? = null,

    var isActive: Boolean = true,

    @CreationTimestamp
    @Column(name = "creation_time")
    var creationTime: LocalDateTime? = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "update_time")
    var updateTime: LocalDateTime? = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long = 0L,
) {
    fun toDto() =
        UserDTO(userId, firstName, lastName, email, urlPhoto)

    companion object {
        fun fromDto(userDTO: UserCreationDTO) =
            User(
                userDTO.firstName,
                userDTO.lastName,
                userDTO.email,
                userDTO.pushNotificationToken,
                userDTO.urlPhoto
            )
    }
}