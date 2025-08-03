package com.yb.rh

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.entities.Car
import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import mu.KotlinLogging
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.dao.DataIntegrityViolationException

@Configuration
class RhServiceConfiguration {

    private val logger = KotlinLogging.logger {}
    
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerModule(KotlinModule.Builder().build())
    }
    


    @Bean
    @Profile("dev") // Only run in development profile
    fun databaseInitializer(
        userRepository: UserRepository,
        carRepository: CarRepository,
        userCarRepository: UserCarRepository,
    ) =
        ApplicationRunner {
            logger.info("DB Init Starting")
            
            // Check if data already exists
            val existingCarsCount = carRepository.count()
            if (existingCarsCount > 0) {
                logger.info("Database already contains $existingCarsCount cars. Skipping initialization.")
                return@ApplicationRunner
            }

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
            
            logger.info("Initializing cars data...")

            // Save cars with error handling
            val carsToSave = listOf(carOne, carTwo, carThree, carFour, carFive, carSix, carSeven)
            var savedCount = 0
            
            for (car in carsToSave) {
                try {
                    // Check if car with this plate number already exists
                    if (carRepository.findByPlateNumber(car.plateNumber) == null) {
                        carRepository.save(car)
                        savedCount++
                        logger.debug("Saved car with plate number: ${car.plateNumber}")
                    } else {
                        logger.debug("Car with plate number ${car.plateNumber} already exists, skipping...")
                    }
                } catch (e: DataIntegrityViolationException) {
                    logger.warn("Failed to save car with plate number ${car.plateNumber}: ${e.message}")
                } catch (e: Exception) {
                    logger.error("Unexpected error saving car with plate number ${car.plateNumber}: ${e.message}")
                }
            }

            logger.info("Cars initialization completed. Saved $savedCount new cars.")
            logger.info("DB Init Done")
        }
}