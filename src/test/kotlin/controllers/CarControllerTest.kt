package controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.controllers.CarsController
import com.yb.rh.entities.Car
import com.yb.rh.entities.CarDTO
import com.yb.rh.error.RHException
import com.yb.rh.services.CarService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

class CarControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var carService: CarService
    private lateinit var carsController: CarsController
    private lateinit var objectMapper: ObjectMapper

    private val testCar = Car(
        plateNumber = "123456",
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    private val testCarDTO = CarDTO(
        plateNumber = "123456",
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        carService = mockk(relaxed = true)
        objectMapper = ObjectMapper()
        carsController = CarsController(carService)
        mockMvc = MockMvcBuilders.standaloneSetup(carsController).build()
    }

    @Test
    fun `test find all cars`() {
        // Set up mock
        every { carService.findAll() } returns listOf(testCar)

        // Perform request
        mockMvc.perform(get("/api/cars/"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].plateNumber").value("123456"))
            .andExpect(jsonPath("$[0].brand").value("TESLA"))
            .andExpect(jsonPath("$[0].model").value("Model 3"))
        
        // Verify service was called
        verify(exactly = 1) { carService.findAll() }
    }

    @Test
    fun `test find by plate number`() {
        // Set up mock
        val resultOk: Result<CarDTO, RHException> = Ok(testCarDTO)
        every { carService.findByPlateNumber("123456") } returns resultOk

        // Perform request
        mockMvc.perform(get("/api/cars/by-plate")
            .param("plateNumber", "123456"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.plateNumber").value("123456"))
            .andExpect(jsonPath("$.brand").value("TESLA"))
            .andExpect(jsonPath("$.model").value("Model 3"))
        
        // Verify service was called
        verify(exactly = 1) { carService.findByPlateNumber("123456") }
    }

    @Test
    fun `test create or update car`() {
        // Set up mock
        val resultOk: Result<Car, RHException> = Ok(testCar)
        every { carService.createOrUpdateCar("123456", 1L) } returns resultOk

        // Perform request
        mockMvc.perform(
            post("/api/cars/car")
                .param("plateNumber", "123456")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.plateNumber").value("123456"))
            .andExpect(jsonPath("$.brand").value("TESLA"))
            .andExpect(jsonPath("$.model").value("Model 3"))
        
        // Verify service was called
        verify(exactly = 1) { carService.createOrUpdateCar("123456", 1L) }
    }
} 