package com.yb.rh.entities

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
@Table(name = "cars_relations")
data class CarsRelations(
    @ManyToOne
    @NotNull
    @JoinColumn(name = "blocking_car_id", referencedColumnName = "id")
    var blockingCar: Car,

    @ManyToOne
    @NotNull
    @JoinColumn(name = "blocked_car_id", referencedColumnName = "id")
    var blockedCar: Car,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
)
