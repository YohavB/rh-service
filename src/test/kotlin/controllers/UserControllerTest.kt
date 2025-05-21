package controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.Ok
import com.yb.rh.controllers.UsersController
import com.yb.rh.entities.User
import com.yb.rh.entities.UserDTO
import com.yb.rh.services.UsersService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UsersControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var usersService: UsersService
    private lateinit var usersController: UsersController
    private lateinit var objectMapper: ObjectMapper

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null
    )

    private val testUserDTO = UserDTO(
        id = 1L,
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null,
        userCars = null
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersService = mockk(relaxed = true)
        objectMapper = ObjectMapper()
        usersController = UsersController(usersService)
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build()
    }

    @Test
    fun `test find all users`() {
        // Set up mock
        every { usersService.findAll() } returns listOf(testUser)

        // Perform request
        mockMvc.perform(get("/api/users/"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].email").value("test@test.com"))
            .andExpect(jsonPath("$[0].firstName").value("Test"))
            .andExpect(jsonPath("$[0].lastName").value("User"))
            
        // Verify service was called
        verify(exactly = 1) { usersService.findAll() }
    }

    @Test
    fun `test find by id`() {
        // Set up mock
        every { usersService.findByUserId(1L) } returns Ok(testUserDTO)

        // Perform request
        mockMvc.perform(get("/api/users/by-id")
            .param("id", "1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@test.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
            
        // Verify service was called
        verify(exactly = 1) { usersService.findByUserId(1L) }
    }

    @Test
    fun `test find by email`() {
        // Set up mock
        every { usersService.findByEmail("test@test.com") } returns Ok(testUserDTO)

        // Perform request
        mockMvc.perform(get("/api/users/by-email")
            .param("mail", "test@test.com"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@test.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
            
        // Verify service was called
        verify(exactly = 1) { usersService.findByEmail("test@test.com") }
    }

    @Test
    fun `test create or update user`() {
        // Set up mock
        every { usersService.createOrUpdateUser(testUserDTO) } returns Ok(testUserDTO)

        // Perform request
        mockMvc.perform(
            post("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("test@test.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
            
        // Verify service was called
        verify(exactly = 1) { usersService.createOrUpdateUser(testUserDTO) }
    }
} 