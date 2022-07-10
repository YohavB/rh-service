package com.yb.rh.entities

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "users_cars")
data class UsersCars(
    @ManyToOne @NotNull
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    var userId: Users,

    @ManyToOne @NotNull
    @JoinColumn(name = "user_car", insertable = false, updatable = false)
    var userCar: Cars,

    @ManyToOne
    @JoinColumn(name = "blocking_car", insertable = false, updatable = false)
    var blockingCar: Cars? = null,

    @ManyToOne
    @JoinColumn(name = "blocked_car", insertable = false, updatable = false)
    var blockedCar: Cars? = null,

    @Id @GeneratedValue
    var id: Long = 0,
) {
    fun blockedBy(blockingCar: Cars?) {
        this.blockingCar = blockingCar
    }

    fun blocking(blockedCar: Cars?) {
        this.blockedCar = blockedCar
    }

    fun unblocked() {
        this.blockingCar = null
    }

    fun unblocking() {
        this.blockedCar = null
    }


    fun toDto() = UsersCarsDTO(userId, userCar, blockingCar, blockedCar)

    companion object {
        fun fromDto(usersCarsDTO: UsersCarsDTO) = UsersCars(
            usersCarsDTO.userId, usersCarsDTO.userCar, usersCarsDTO.blockingCar, usersCarsDTO.blockedCar
        )
    }
}

data class UsersCarsDTO(
    var userId: Users,
    var userCar: Cars,
    var blockingCar: Cars?,
    var blockedCar: Cars?
)
