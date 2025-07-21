package com.yb.rh.entities

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "cars_relations")
data class CarsRelations(
    @ManyToOne
    @NotNull
    @JoinColumn(name = "blocking_car", referencedColumnName = "plate_number")
    var blockingCar: Car,

    @ManyToOne
    @NotNull
    @JoinColumn(name = "blocked_car", referencedColumnName = "plate_number")
    var blockedCar: Car,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
)
