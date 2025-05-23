package com.yb.rh

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.entities.Car
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RhServiceConfiguration {

    @Bean
    fun databaseInitializer(
        usersRepository: UsersRepository,
        carsRepository: CarsRepository,
        usersCarsRepository: UsersCarsRepository,
    ) =
        ApplicationRunner {
            println("DB Init Starting ")
//            val userOne = User("User1", "One", "login1", "one@gmail.com", "1111")
//            val userTwo = User("User2", "Two", "login2", "two@gmail.com", "2222")
//            val userThree = User("User3", "Three", "login3", "three@gmail.com", "3333")
//            val userFour = User("User4", "Four", "login4", "four@gmail.com", "4444")
//            val userFive = User("User5", "Five", "login5", "five@gmail.com", "5555")

            val carOne = Car(
                "11111111",
                Countries.IL,
                Brands.AUDI,
                "TT",
                Colors.WHITE,
                null
            )
            val carTwo = Car(
                "22222222",
                Countries.IL,
                Brands.AUDI,
                "Q7",
                Colors.WHITE,
                null
            )
            val carThree = Car(
                "33333333",
                Countries.IL,
                Brands.AUDI,
                "R8",
                Colors.BLACK,
                null
            )
            val carFour = Car(
                "44444444",
                Countries.IL,
                Brands.TESLA,
                "S",
                Colors.BLACK,
                null
            )
            val carFive = Car(
                "555555555",
                Countries.IL,
                Brands.TESLA,
                "3",
                Colors.WHITE,
                null
            )
            val carSix = Car(
                "6666666",
                Countries.IL,
                Brands.TESLA,
                "X",
                Colors.BLACK,
                null
            )

            val carSeven = Car(
                "7777777",
                Countries.IL,
                Brands.TESLA,
                "Y",
                Colors.WHITE,
                null
            )
            println("users Init Done")

//            usersRepository.save(userOne)
//            usersRepository.save(userTwo)
//            usersRepository.save(userThree)
            println("cars Init Done")

            carsRepository.save(carOne)
            carsRepository.save(carTwo)
            carsRepository.save(carThree)
            println("users cars Init Done")

//            usersCarsRepository.save((UsersCars(userOne, carOne, carTwo, null)))
//            usersCarsRepository.save((UsersCars(userTwo, carTwo, null, carOne)))
//            usersCarsRepository.save((UsersCars(userThree, carThree)))

            println("DB Init Done")
//            usersCarsRepository.deleteAll()
//            carsRepository.deleteAll()
//            usersRepository.deleteAll()

//            val userCars0 = usersCarsRepository.save((UsersCars(userOne, carOne, carTwo, null)))
//            val userCars1 = usersCarsRepository.save((UsersCars(userTwo, carTwo, null, carOne)))
//            val userCars2 = usersCarsRepository.save((UsersCars(userThree, carThree)))
//            val userCars3 = usersCarsRepository.save((UsersCars(userFour, carFour)))
//            val userCars4 = usersCarsRepository.save((UsersCars(userFive, carFive)))
//            val userCars5 = usersCarsRepository.save((UsersCars(userFive, carSix)))
//            val userCars6 = usersCarsRepository.save((UsersCars(userFour, carOne)))
//            println(userCars0)
//            println(usersCarsRepository.findAll())
//            println(usersCarsRepository.findByUserId(userOne))
//            println(usersCarsRepository.findByUserCar(carTwo))
        }
}