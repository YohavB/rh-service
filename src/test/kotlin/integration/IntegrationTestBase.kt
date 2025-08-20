package com.yb.rh.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.yb.rh.RhServiceApplication
import com.yb.rh.TestSecurityConfig
import com.yb.rh.dtos.CarDTO
import com.yb.rh.enum.Brands
import com.yb.rh.enum.Colors
import com.yb.rh.enum.Countries
import com.yb.rh.services.CarApiService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime
import javax.sql.DataSource

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [RhServiceApplication::class, TestSecurityConfig::class]
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
    protected lateinit var carApi: CarApiService

    @MockBean
    protected lateinit var currentUserService: com.yb.rh.services.CurrentUserService

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
        
        // Reset auto-increment counters for H2 database
        jdbcTemplate.execute("ALTER TABLE cars ALTER COLUMN id RESTART WITH 1")
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1")
        jdbcTemplate.execute("ALTER TABLE users_cars ALTER COLUMN id RESTART WITH 1")
        jdbcTemplate.execute("ALTER TABLE cars_relations ALTER COLUMN id RESTART WITH 1")
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
            manufacturingYear = 2020,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        val car2DTO = CarDTO(
            id = 2L,
            plateNumber = "DEF456",
            country = Countries.IL,
            brand = Brands.HONDA,
            model = "Civic",
            color = Colors.BLACK,
            manufacturingYear = 2021,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        val car3DTO = CarDTO(
            id = 3L,
            plateNumber = "GHI789",
            country = Countries.IL,
            brand = Brands.BMW,
            model = "X3",
            color = Colors.BLUE,
            manufacturingYear = 2022,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        val car4DTO = CarDTO(
            id = 4L,
            plateNumber = "JKL012",
            country = Countries.IL,
            brand = Brands.MERCEDES,
            model = "C-Class",
            color = Colors.SILVER,
            manufacturingYear = 2021,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        val car5DTO = CarDTO(
            id = 5L,
            plateNumber = "MNO345",
            country = Countries.IL,
            brand = Brands.AUDI,
            model = "A4",
            color = Colors.RED,
            manufacturingYear = 2020,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        val car6DTO = CarDTO(
            id = 6L,
            plateNumber = "PQR678",
            country = Countries.IL,
            brand = Brands.VOLKSWAGEN,
            model = "Golf",
            color = Colors.GREEN,
            manufacturingYear = 2022,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1),
            hasOwner = false
        )
        
        // Mock the CarApi to return cars with the requested plate number
        Mockito.`when`(carApi.getCarInfo("CAR123", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "CAR123"))
        Mockito.`when`(carApi.getCarInfo("CAR001", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "CAR001"))
        Mockito.`when`(carApi.getCarInfo("CAR002", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "CAR002"))
        Mockito.`when`(carApi.getCarInfo("SHARED123", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "SHARED123"))
        Mockito.`when`(carApi.getCarInfo("REMOVE123", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "REMOVE123"))
        Mockito.`when`(carApi.getCarInfo("DUPLICATE123", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "DUPLICATE123"))
        Mockito.`when`(carApi.getCarInfo("TEST123", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "TEST123"))
        Mockito.`when`(carApi.getCarInfo("BLOCK123", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "BLOCK123"))
        Mockito.`when`(carApi.getCarInfo("BLOCKED123", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "BLOCKED123"))
        Mockito.`when`(carApi.getCarInfo("FREE123", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "FREE123"))
        Mockito.`when`(carApi.getCarInfo("NOOWNER123", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "NOOWNER123"))
        Mockito.`when`(carApi.getCarInfo("BLOCKING3", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "BLOCKING3"))
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
        
        // EndToEndIntegrationTest mocks
        Mockito.`when`(carApi.getCarInfo("E2E001", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "E2E001"))
        Mockito.`when`(carApi.getCarInfo("E2E002", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "E2E002"))
        Mockito.`when`(carApi.getCarInfo("E2E003", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "E2E003"))
        Mockito.`when`(carApi.getCarInfo("DEACTIVATE123", Countries.IL)).thenReturn(car4DTO.copy(plateNumber = "DEACTIVATE123"))
        Mockito.`when`(carApi.getCarInfo("CLEANUP001", Countries.IL)).thenReturn(car5DTO.copy(plateNumber = "CLEANUP001"))
        Mockito.`when`(carApi.getCarInfo("CLEANUP002", Countries.IL)).thenReturn(car6DTO.copy(plateNumber = "CLEANUP002"))
        
        // Chain scenario mocks
        for (i in 1..5) {
            Mockito.`when`(carApi.getCarInfo("CHAIN00$i", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "CHAIN00$i"))
        }
        
        // MainServiceIntegrationTest mocks
        Mockito.`when`(carApi.getCarInfo("CAR003", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "CAR003"))
        Mockito.`when`(carApi.getCarInfo("SINGLE", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "SINGLE"))
        Mockito.`when`(carApi.getCarInfo("CHAIN1", Countries.IL)).thenReturn(car1DTO.copy(plateNumber = "CHAIN1"))
        Mockito.`when`(carApi.getCarInfo("CHAIN2", Countries.IL)).thenReturn(car2DTO.copy(plateNumber = "CHAIN2"))
        Mockito.`when`(carApi.getCarInfo("CHAIN3", Countries.IL)).thenReturn(car3DTO.copy(plateNumber = "CHAIN3"))
    }

    protected fun setupCurrentUser(userId: Long) {
        // Get user details from the database
        val dbUser = getRowFromTable("users", userId)
        requireNotNull(dbUser) { "User with ID $userId not found in database" }
        
        // Create a user entity to return with actual data from database
        val user = com.yb.rh.entities.User(
            firstName = dbUser["first_name"] as String,
            lastName = dbUser["last_name"] as String,
            email = dbUser["email"] as String,
            pushNotificationToken = (dbUser["push_notification_token"] as String?) ?: "",
            urlPhoto = dbUser["url_photo"] as String?,
            isActive = dbUser["is_active"] as Boolean,
            userId = userId
        )
        
        // Mock the currentUserService to return this user
        Mockito.`when`(currentUserService.getCurrentUser()).thenReturn(user)
        Mockito.`when`(currentUserService.getCurrentUserId()).thenReturn(userId)
        Mockito.`when`(currentUserService.getCurrentUserOrNull()).thenReturn(user)
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

    protected fun performDelete(url: String, body: Any): String {
        return mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
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

    protected fun createUserInDatabase(email: String, firstName: String, lastName: String, pushNotificationToken: String): Long {
        val sql = """
            INSERT INTO users (email, first_name, last_name, push_notification_token, is_active, creation_time, update_time)
            VALUES (?, ?, ?, ?, ?, NOW(), NOW())
        """.trimIndent()
        
        jdbcTemplate.update(sql, email, firstName, lastName, pushNotificationToken, true)
        
        val userId = jdbcTemplate.queryForObject("SELECT user_id FROM users WHERE email = ?", Long::class.java, email)
        return userId ?: throw RuntimeException("User not found after creation")
    }
} 