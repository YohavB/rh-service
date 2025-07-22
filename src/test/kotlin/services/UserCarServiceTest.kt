package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.repositories.UserCarRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserCarServiceTest {

    private lateinit var userCarRepository: UserCarRepository
    private lateinit var userCarService: UserCarService

    @BeforeEach
    fun setUp() {
        userCarRepository = mockk()
        userCarService = UserCarService(userCarRepository)
    }

    @Test
    fun `test createUserCar success`() {
        val user = mockk<com.yb.rh.entities.User>()
        val car = mockk<com.yb.rh.entities.Car>()
        val userCar = mockk<com.yb.rh.entities.UserCar>()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { user.userId } returns 1L
        every { car.id } returns 1L
        every { userCarRepository.findByUserAndCar(user, car) } returns null
        every { userCarRepository.save(any()) } returns userCar
        every { userCarRepository.findAllByUser(user) } returns listOf(userCar)
        every { userCar.toDto() } returns TestObjectBuilder.getUserCarDTO()
        every { user.toDto() } returns TestObjectBuilder.getUserDTO()

        val result = userCarService.createUserCar(user, car)

        assertNotNull(result)
        assertEquals(userCarsDTO.user.id, result.user.id)
        verify { 
            userCarRepository.findByUserAndCar(user, car)
            userCarRepository.save(any())
            userCarRepository.findAllByUser(user)
        }
    }

    @Test
    fun `test getUsersCarsByUser success`() {
        val user = mockk<com.yb.rh.entities.User>()
        val userCars = (1..3).map { mockk<com.yb.rh.entities.UserCar>() }
        val userCarDTOs = (1..3).map { TestObjectBuilder.getUserCarDTO() }
        
        every { user.userId } returns 1L
        every { userCarRepository.findAllByUser(user) } returns userCars
        userCars.forEachIndexed { index, userCar ->
            every { userCar.toDto() } returns userCarDTOs[index]
        }

        val result = userCarService.getUsersCarsByUser(user)

        assertNotNull(result)
        assertEquals(3, result.size)
        verify { userCarRepository.findAllByUser(user) }
    }

    @Test
    fun `test getUsersCarsByUser empty`() {
        val user = mockk<com.yb.rh.entities.User>()
        
        every { user.userId } returns 1L
        every { userCarRepository.findAllByUser(user) } returns emptyList()

        val result = userCarService.getUsersCarsByUser(user)

        assertNotNull(result)
        assertEquals(0, result.size)
        verify { userCarRepository.findAllByUser(user) }
    }

    @Test
    fun `test getUsersCarsByCar success`() {
        val car = mockk<com.yb.rh.entities.Car>()
        val userCars = (1..2).map { mockk<com.yb.rh.entities.UserCar>() }
        val userCarDTOs = (1..2).map { TestObjectBuilder.getUserCarDTO() }
        
        every { car.id } returns 1L
        every { userCarRepository.findAllByCar(car) } returns userCars
        userCars.forEachIndexed { index, userCar ->
            every { userCar.toDto() } returns userCarDTOs[index]
        }

        val result = userCarService.getUsersCarsByCar(car)

        assertNotNull(result)
        assertEquals(2, result.size)
        verify { userCarRepository.findAllByCar(car) }
    }

    @Test
    fun `test getUsersCarsByCar empty`() {
        val car = mockk<com.yb.rh.entities.Car>()
        
        every { car.id } returns 1L
        every { userCarRepository.findAllByCar(car) } returns emptyList()

        val result = userCarService.getUsersCarsByCar(car)

        assertNotNull(result)
        assertEquals(0, result.size)
        verify { userCarRepository.findAllByCar(car) }
    }

    @Test
    fun `test getUserCarByUserAndCar success`() {
        val user = mockk<com.yb.rh.entities.User>()
        val car = mockk<com.yb.rh.entities.Car>()
        val userCar = mockk<com.yb.rh.entities.UserCar>()
        val userCarDTO = TestObjectBuilder.getUserCarDTO()
        
        every { user.userId } returns 1L
        every { car.id } returns 1L
        every { userCarRepository.findByUserAndCar(user, car) } returns userCar
        every { userCar.toDto() } returns userCarDTO

        val result = userCarService.getUserCarByUserAndCar(user, car)

        assertNotNull(result)
        assertEquals(userCarDTO.user.id, result.user.id)
        verify { userCarRepository.findByUserAndCar(user, car) }
    }

    @Test
    fun `test getUserCarByUserAndCar not found`() {
        val user = mockk<com.yb.rh.entities.User>()
        val car = mockk<com.yb.rh.entities.Car>()
        
        every { user.userId } returns 1L
        every { car.id } returns 1L
        every { userCarRepository.findByUserAndCar(user, car) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userCarService.getUserCarByUserAndCar(user, car)
        }
        verify { userCarRepository.findByUserAndCar(user, car) }
    }

    @Test
    fun `test getUserCarsByUser success`() {
        val user = mockk<com.yb.rh.entities.User>()
        val userCars = (1..2).map { mockk<com.yb.rh.entities.UserCar>() }
        val userCarDTOs = (1..2).map { TestObjectBuilder.getUserCarDTO() }
        val userDTO = TestObjectBuilder.getUserDTO()
        
        every { user.userId } returns 1L
        every { userCarRepository.findAllByUser(user) } returns userCars
        every { user.toDto() } returns userDTO
        userCars.forEachIndexed { index, userCar ->
            every { userCar.toDto() } returns userCarDTOs[index]
        }

        val result = userCarService.getUserCarsByUser(user)

        assertNotNull(result)
        assertEquals(userDTO.id, result.user.id)
        assertEquals(2, result.cars.size)
        verify { 
            userCarRepository.findAllByUser(user)
            user.toDto()
        }
    }

    @Test
    fun `test getCarUsersByCar success`() {
        val car = mockk<com.yb.rh.entities.Car>()
        val userCars = (1..2).map { mockk<com.yb.rh.entities.UserCar>() }
        val userCarDTOs = (1..2).map { TestObjectBuilder.getUserCarDTO() }
        val carDTO = TestObjectBuilder.getCarDTO()
        
        every { car.id } returns 1L
        every { userCarRepository.findAllByCar(car) } returns userCars
        every { car.toDto() } returns carDTO
        userCars.forEachIndexed { index, userCar ->
            every { userCar.toDto() } returns userCarDTOs[index]
        }

        val result = userCarService.getCarUsersByCar(car)

        assertNotNull(result)
        assertEquals(carDTO.id, result.car.id)
        assertEquals(2, result.users.size)
        verify { 
            userCarRepository.findAllByCar(car)
            car.toDto()
        }
    }

    @Test
    fun `test deleteUserCar success`() {
        val user = mockk<com.yb.rh.entities.User>()
        val car = mockk<com.yb.rh.entities.Car>()
        val userCar = mockk<com.yb.rh.entities.UserCar>()
        val userCarsDTO = TestObjectBuilder.getUserCarsDTO()
        
        every { user.userId } returns 1L
        every { car.id } returns 1L
        every { userCarRepository.findByUserAndCar(user, car) } returns userCar
        every { userCarRepository.delete(userCar) } just Runs
        every { userCarRepository.findAllByUser(user) } returns emptyList()
        every { user.toDto() } returns TestObjectBuilder.getUserDTO()

        val result = userCarService.deleteUserCar(user, car)

        assertNotNull(result)
        assertEquals(userCarsDTO.user.id, result.user.id)
        verify { 
            userCarRepository.findByUserAndCar(user, car)
            userCarRepository.delete(userCar)
            userCarRepository.findAllByUser(user)
        }
    }

    @Test
    fun `test deleteUserCar not found`() {
        val user = mockk<com.yb.rh.entities.User>()
        val car = mockk<com.yb.rh.entities.Car>()
        
        every { user.userId } returns 1L
        every { car.id } returns 1L
        every { userCarRepository.findByUserAndCar(user, car) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userCarService.deleteUserCar(user, car)
        }
        verify { userCarRepository.findByUserAndCar(user, car) }
        verify(exactly = 0) { userCarRepository.delete(any()) }
    }
} 