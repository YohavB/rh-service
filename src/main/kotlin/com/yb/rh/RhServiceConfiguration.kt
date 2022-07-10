package com.yb.rh

import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.entities.Cars
import com.yb.rh.entities.Users
import com.yb.rh.entities.UsersCars
import com.yb.rh.repositorties.CarsRepository
import com.yb.rh.repositorties.UsersCarsRepository
import com.yb.rh.repositorties.UsersRepository
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
            val userOne = usersRepository.save((Users("User1", "One", "login1", "one@gmail.com", "1111")))
            val userTwo = usersRepository.save((Users("User2", "Two", "login2", "two@gmail.com", "2222")))
            val userThree = usersRepository.save((Users("User3", "Three", "login3", "three@gmail.com", "3333")))
            val userFour = usersRepository.save((Users("User4", "Four", "login4", "four@gmail.com", "4444")))
            val userFive = usersRepository.save((Users("User5", "Five", "login5", "five@gmail.com", "5555")))

            val carOne = carsRepository.save(
                (Cars(
                    "11111111",
                    Brands.AUDI,
                    "TT",
                    Colors.WHITE,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carTwo = carsRepository.save(
                (Cars(
                    "22222222",
                    Brands.AUDI,
                    "Q7",
                    Colors.WHITE,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carThree = carsRepository.save(
                (Cars(
                    "33333333",
                    Brands.AUDI,
                    "R8",
                    Colors.BLACK,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carFour = carsRepository.save(
                (Cars(
                    "44444444",
                    Brands.TESLA,
                    "S",
                    Colors.BLACK,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carFive = carsRepository.save(
                (Cars(
                    "555555555",
                    Brands.TESLA,
                    "3",
                    Colors.WHITE,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carSix = carsRepository.save(
                (Cars(
                    "6666666",
                    Brands.TESLA,
                    "X",
                    Colors.BLACK,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            val carSeven = carsRepository.save(
                (Cars(
                    "7777777",
                    Brands.TESLA,
                    "Y",
                    Colors.WHITE,
                    null,
                    isBlocking = false,
                    isBlocked = false
                ))
            )
            println("DB Init Done")

            val userCars0 = usersCarsRepository.save((UsersCars(userOne,carOne)))
            val userCars1 = usersCarsRepository.save((UsersCars(userTwo,carTwo)))
            val userCars2 = usersCarsRepository.save((UsersCars(userThree,carThree)))
            val userCars3 = usersCarsRepository.save((UsersCars(userFour,carFour)))
            val userCars4 = usersCarsRepository.save((UsersCars(userFive,carFive)))
            val userCars5 = usersCarsRepository.save((UsersCars(userFive,carSix)))
            val userCars6 = usersCarsRepository.save((UsersCars(userFour,carOne)))
        }
}