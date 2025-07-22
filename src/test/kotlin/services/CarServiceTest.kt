package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.common.Countries
import com.yb.rh.repositories.CarRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CarServiceTest {
    private lateinit var carRepository: CarRepository
    private lateinit var carApi: CarApi
    private lateinit var carsRelationsService: CarsRelationsService
    private lateinit var userCarService: UserCarService
    private lateinit var userService: UserService
    private lateinit var carService: CarService

    @BeforeEach
    fun setUp() {
        carRepository = mockk()
        carApi = mockk()
        carsRelationsService = mockk()
        userCarService = mockk()
        userService = mockk()
        carService = CarService(carRepository, carApi)
    }

    @Test
    fun `test getCarOrCreateRequest existing car`() {
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO()
        val existingCar = mockk<com.yb.rh.entities.Car>()
        val carDTO = TestObjectBuilder.getCarDTO()

        every { carRepository.findByPlateNumber(findCarRequestDTO.plateNumber) } returns existingCar
        every { existingCar.toDto() } returns carDTO

        val result = carService.getCarOrCreateRequest(findCarRequestDTO)

        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        assertEquals(carDTO.plateNumber, result.plateNumber)
        verify { carRepository.findByPlateNumber(findCarRequestDTO.plateNumber) }
    }

    @Test
    fun `test getCarOrCreateRequest new car`() {
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO()
        val carDTO = TestObjectBuilder.getCarDTO()
        val newCar = mockk<com.yb.rh.entities.Car>()

        every { carRepository.findByPlateNumber(findCarRequestDTO.plateNumber) } returns null
        every { carApi.getCarInfo(findCarRequestDTO.plateNumber, findCarRequestDTO.country) } returns carDTO
        every { carRepository.save(any()) } returns newCar
        every { newCar.toDto() } returns carDTO

        val result = carService.getCarOrCreateRequest(findCarRequestDTO)

        assertNotNull(result)
        assertEquals(carDTO.id, result.id)
        verify { carRepository.findByPlateNumber(findCarRequestDTO.plateNumber) }
        verify { carApi.getCarInfo(findCarRequestDTO.plateNumber, findCarRequestDTO.country) }
    }

    @Test
    fun `test getCarById success`() {
        val carId = 1L
        val car = mockk<com.yb.rh.entities.Car>()

        every { carRepository.findCarById(carId) } returns car

        val result = carService.getCarById(carId)

        assertNotNull(result)
        assertEquals(car, result)
        verify { carRepository.findCarById(carId) }
    }

    @Test
    fun `test getCarById not found`() {
        val carId = 1L

        every { carRepository.findCarById(carId) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            carService.getCarById(carId)
        }
        verify { carRepository.findCarById(carId) }
    }



    @Test
    fun `test getCarOrCreate existing car`() {
        val plateNumber = "ABC123"
        val country = Countries.IL
        val existingCar = mockk<com.yb.rh.entities.Car>()

        every { carRepository.findByPlateNumber(plateNumber) } returns existingCar

        val result = carService.getCarOrCreate(plateNumber, country)

        assertNotNull(result)
        assertEquals(existingCar, result)
        verify { carRepository.findByPlateNumber(plateNumber) }
    }

    @Test
    fun `test getCarOrCreate new car`() {
        val plateNumber = "ABC123"
        val country = Countries.IL
        val carDTO = TestObjectBuilder.getCarDTO()
        val newCar = mockk<com.yb.rh.entities.Car>()

        every { carRepository.findByPlateNumber(plateNumber) } returns null
        every { carApi.getCarInfo(plateNumber, country) } returns carDTO
        every { carRepository.save(any()) } returns newCar

        val result = carService.getCarOrCreate(plateNumber, country)

        assertNotNull(result)
        assertEquals(newCar, result)
        verify { carRepository.findByPlateNumber(plateNumber) }
        verify { carApi.getCarInfo(plateNumber, country) }
    }

    @Test
    fun `test getCarOrCreate with different country`() {
        val plateNumber = "XYZ789"
        val country = Countries.IL
        val carDTO = TestObjectBuilder.getCarDTO(plateNumber = plateNumber)
        val newCar = mockk<com.yb.rh.entities.Car>()

        every { carRepository.findByPlateNumber(plateNumber) } returns null
        every { carApi.getCarInfo(plateNumber, country) } returns carDTO
        every { carRepository.save(any()) } returns newCar

        val result = carService.getCarOrCreate(plateNumber, country)

        assertNotNull(result)
        assertEquals(newCar, result)
        verify { carRepository.findByPlateNumber(plateNumber) }
        verify { carApi.getCarInfo(plateNumber, country) }
    }

    @Test
    fun `test getCarOrCreateRequest with blank plate number`() {
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = "")

        assertThrows<com.yb.rh.error.RHException> {
            carService.getCarOrCreateRequest(findCarRequestDTO)
        }
    }

    @Test
    fun `test getCarOrCreateRequest with very long plate number`() {
        val longPlateNumber = "A".repeat(51)
        val findCarRequestDTO = TestObjectBuilder.getFindCarRequestDTO(plateNumber = longPlateNumber)

        assertThrows<com.yb.rh.error.RHException> {
            carService.getCarOrCreateRequest(findCarRequestDTO)
        }
    }



    @Test
    fun `test validatePlateNumber with valid plate number`() {
        val validPlateNumber = "ABC123"

        carService.validatePlateNumber(validPlateNumber)

        // Should not throw any exception
    }

    @Test
    fun `test validatePlateNumber with blank plate number`() {
        val blankPlateNumber = "   "

        assertThrows<com.yb.rh.error.RHException> {
            carService.validatePlateNumber(blankPlateNumber)
        }
    }

    @Test
    fun `test validatePlateNumber with empty plate number`() {
        val emptyPlateNumber = ""

        assertThrows<com.yb.rh.error.RHException> {
            carService.validatePlateNumber(emptyPlateNumber)
        }
    }

    @Test
    fun `test validatePlateNumber with too long plate number`() {
        val longPlateNumber = "A".repeat(51)

        assertThrows<com.yb.rh.error.RHException> {
            carService.validatePlateNumber(longPlateNumber)
        }
    }

    @Test
    fun `test validatePlateNumber with maximum length plate number`() {
        val maxLengthPlateNumber = "A".repeat(50)

        carService.validatePlateNumber(maxLengthPlateNumber)

        // Should not throw any exception
    }
} 