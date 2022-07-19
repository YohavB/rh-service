package com.yb.rh.entities

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "users_cars")
data class UsersCars(
    @ManyToOne @NotNull
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    var user: User,

    @ManyToOne @NotNull
    @JoinColumn(name = "user_car", referencedColumnName = "plate_number")
    var car: Car,

    @ManyToOne
    @JoinColumn(name = "blocking_car", referencedColumnName = "plate_number")
    var blockingCar: Car? = null,

    @ManyToOne
    @JoinColumn(name = "blocked_car", referencedColumnName = "plate_number")
    var blockedCar: Car? = null,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
) {
    fun blockedBy(blockingCar: Car?) {
        this.blockingCar = blockingCar
    }

    fun blocking(blockedCar: Car?) {
        this.blockedCar = blockedCar
    }

    fun unblocked() {
        this.blockingCar = null
    }

    fun unblocking() {
        this.blockedCar = null
    }


    fun toDto() = UsersCarsDTO(user.userId, car.plateNumber, blockingCar?.plateNumber, blockedCar?.plateNumber)

//    companion object {
//        fun fromDto(usersCarsDTO: UsersCarsDTO) = UsersCars(
//            usersCarsDTO.userId, usersCarsDTO.userCar, usersCarsDTO.blockingCar, usersCarsDTO.blockedCar
//        )
//    }
}

data class UsersCarsDTO(
    var userId: Long,
    var userCar: String,
    var blockingCar: String?,
    var blockedCar: String?
)
