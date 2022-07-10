package com.yb.rh.entities

import javax.persistence.*

@Entity
@Table(name = "users")
data class Users(
    var firstName: String,
    var lastName: String,
    var login: String,
    @Column(unique=true)
    var mail: String,
    @Column(unique=true)
    var phone: String,
    @Id @GeneratedValue
    val userId: Long = 0
) {
    fun toDto() = UsersDTO(firstName, lastName, login, mail, phone, userId)

    companion object {
        fun fromDto(usersDTO: UsersDTO) =
            Users(usersDTO.firstName, usersDTO.lastName, usersDTO.login, usersDTO.mail, usersDTO.phone, usersDTO.userId)
    }
}

data class UsersDTO(
    var firstName: String,
    var lastName: String,
    var login: String,
    var mail: String,
    var phone: String,
    val userId: Long
)