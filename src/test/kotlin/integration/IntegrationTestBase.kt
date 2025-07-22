package com.yb.rh.integration

import com.yb.rh.RhServiceApplication
import com.yb.rh.services.CarApi
import com.yb.rh.dtos.CarDTO
import com.yb.rh.common.Countries
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.mockito.Mockito
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate

import java.time.LocalDateTime

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [RhServiceApplication::class]
)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class IntegrationTestBase {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    protected lateinit var dataSource: DataSource

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @MockBean
    protected lateinit var carApi: CarApi

    protected lateinit var mockMvc: MockMvc
    protected lateinit var jdbcTemplate: JdbcTemplate



    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build()
        
        jdbcTemplate = JdbcTemplate(dataSource)
        
        // Clear all tables before each test
        clearDatabase()
        
        // Setup default mocks
        setupDefaultMocks()
    }

    protected fun clearDatabase() {
        jdbcTemplate.execute("DELETE FROM cars_relations")
        jdbcTemplate.execute("DELETE FROM users_cars")
        jdbcTemplate.execute("DELETE FROM cars")
        jdbcTemplate.execute("DELETE FROM users")
    }

    protected fun setupDefaultMocks() {
        // Create different car responses for different plate numbers
        val car1DTO = CarDTO(
            id = 1L,
            plateNumber = "ABC123",
            country = Countries.IL,
            brand = Brands.TOYOTA,
            model = "Corolla",
            color = Colors.WHITE,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        val car2DTO = CarDTO(
            id = 2L,
            plateNumber = "DEF456",
            country = Countries.IL,
            brand = Brands.HONDA,
            model = "Civic",
            color = Colors.BLACK,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        val car3DTO = CarDTO(
            id = 3L,
            plateNumber = "GHI789",
            country = Countries.IL,
            brand = Brands.BMW,
            model = "X3",
            color = Colors.BLUE,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        val car4DTO = CarDTO(
            id = 4L,
            plateNumber = "JKL012",
            country = Countries.IL,
            brand = Brands.MERCEDES,
            model = "C-Class",
            color = Colors.SILVER,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        val car5DTO = CarDTO(
            id = 5L,
            plateNumber = "MNO345",
            country = Countries.IL,
            brand = Brands.AUDI,
            model = "A4",
            color = Colors.RED,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        val car6DTO = CarDTO(
            id = 6L,
            plateNumber = "PQR678",
            country = Countries.IL,
            brand = Brands.VOLKSWAGEN,
            model = "Golf",
            color = Colors.GREEN,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )
        
        // Mock the CarApi to return cars with the requested plate number
        Mockito.`when`(carApi.getCarInfo("CAR123", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "CAR123"))
        Mockito.`when`(carApi.getCarInfo("CAR001", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "CAR001"))
        Mockito.`when`(carApi.getCarInfo("REMOVE123", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "REMOVE123"))
        Mockito.`when`(carApi.getCarInfo("TEST123", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "TEST123"))
        Mockito.`when`(carApi.getCarInfo("BLOCK123", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "BLOCK123"))
        Mockito.`when`(carApi.getCarInfo("BLOCKED123", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "BLOCKED123"))
        Mockito.`when`(carApi.getCarInfo("FREE123", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "FREE123"))
        Mockito.`when`(carApi.getCarInfo("NOOWNER123", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "NOOWNER123"))
        Mockito.`when`(carApi.getCarInfo("BLOCKING3", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "BLOCKING3"))
        Mockito.`when`(carApi.getCarInfo("ABC123", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "ABC123"))
        Mockito.`when`(carApi.getCarInfo("XYZ789", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "XYZ789"))
        Mockito.`when`(carApi.getCarInfo("GET123", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "GET123"))
        Mockito.`when`(carApi.getCarInfo("ID456", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "ID456"))
        Mockito.`when`(carApi.getCarInfo("ALL001", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "ALL001"))
        Mockito.`when`(carApi.getCarInfo("ALL002", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "ALL002"))
        
        // CarRelationsIntegrationTest mocks
        Mockito.`when`(carApi.getCarInfo("BLOCK001", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "BLOCK001"))
        Mockito.`when`(carApi.getCarInfo("BLOCK002", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "BLOCK002"))
        Mockito.`when`(carApi.getCarInfo("REL001", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "REL001"))
        Mockito.`when`(carApi.getCarInfo("REL002", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "REL002"))
        Mockito.`when`(carApi.getCarInfo("REL003", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "REL003"))
        Mockito.`when`(carApi.getCarInfo("BLOCKED001", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "BLOCKED001"))
        Mockito.`when`(carApi.getCarInfo("BLOCKED002", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "BLOCKED002"))
        Mockito.`when`(carApi.getCarInfo("BLOCKED003", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "BLOCKED003"))
        Mockito.`when`(carApi.getCarInfo("DELETE001", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "DELETE001"))
        Mockito.`when`(carApi.getCarInfo("DELETE002", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "DELETE002"))
        Mockito.`when`(carApi.getCarInfo("DUPL001", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "DUPL001"))
        Mockito.`when`(carApi.getCarInfo("DUPL002", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "DUPL002"))
        Mockito.`when`(carApi.getCarInfo("SELF001", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "SELF001"))
    }

    protected fun getBaseUrl(): String = "http://localhost:$port"

    protected fun performGet(url: String): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    }

    protected fun performPost(url: String, body: Any): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    }

    protected fun performPost(url: String, body: Any, expectedStatus: Int): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
        .andExpect(MockMvcResultMatchers.status().`is`(expectedStatus))
        .andReturn()
        .response
        .contentAsString
    }

    protected fun performPut(url: String, body: Any): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    }

    protected fun performPut(url: String): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    }

    protected fun performDelete(url: String): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    }

    protected fun countRowsInTable(tableName: String): Int {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM $tableName", Int::class.java) ?: 0
    }

    protected fun getRowFromTable(tableName: String, id: Long): Map<String, Any>? {
        val idColumn = when (tableName) {
            "users" -> "user_id"
            "cars" -> "id"
            "users_cars" -> "id"
            "cars_relations" -> "id"
            else -> "id"
        }
        return jdbcTemplate.queryForMap("SELECT * FROM $tableName WHERE $idColumn = ?", id)
    }

    protected fun getAllRowsFromTable(tableName: String): List<Map<String, Any>> {
        return jdbcTemplate.queryForList("SELECT * FROM $tableName")
    }
} 