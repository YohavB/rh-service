package services.simple

import com.yb.rh.entities.UsersCars
import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import com.yb.rh.services.CarService
import com.yb.rh.services.NotificationService
import com.yb.rh.services.UserCarService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Basic test for UsersCarsService that focuses on minimal functionality without complex mocking
 */
class UsersCarsServiceBasicTest {

    @Test
    fun `getAllUsersCars should return all users-cars relationships from repository`() {
        // Given
        val userCarRepository = mockk<UserCarRepository>()
        val carRepository = mockk<CarRepository>()
        val carService = mockk<CarService>()
        val userRepository = mockk<UserRepository>()
        val notificationService = mockk<NotificationService>()
        
        val testUsersCars = mockk<UsersCars>()
        val usersCarsList = listOf(testUsersCars)
        
        every { userCarRepository.findAll() } returns usersCarsList
        
        val userCarService = UserCarService(
            userCarRepository,
            carRepository,
            carService,
            userRepository,
            notificationService
        )
        
        // When
        val result = userCarService.getAllUsersCars()
        
        // Then
        assertEquals(usersCarsList, result)
        verify { userCarRepository.findAll() }
    }
} 