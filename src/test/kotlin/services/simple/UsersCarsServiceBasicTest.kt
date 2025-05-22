package services.simple

import com.yb.rh.entities.UsersCars
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.CarService
import com.yb.rh.services.NotificationService
import com.yb.rh.services.UsersCarsService
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
        val usersCarsRepository = mockk<UsersCarsRepository>()
        val carsRepository = mockk<CarsRepository>()
        val carService = mockk<CarService>()
        val usersRepository = mockk<UsersRepository>()
        val notificationService = mockk<NotificationService>()
        
        val testUsersCars = mockk<UsersCars>()
        val usersCarsList = listOf(testUsersCars)
        
        every { usersCarsRepository.findAll() } returns usersCarsList
        
        val usersCarsService = UsersCarsService(
            usersCarsRepository,
            carsRepository,
            carService,
            usersRepository,
            notificationService
        )
        
        // When
        val result = usersCarsService.getAllUsersCars()
        
        // Then
        assertEquals(usersCarsList, result)
        verify { usersCarsRepository.findAll() }
    }
} 