package controllers

import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.Countries
import com.yb.rh.common.UserStatus
import com.yb.rh.controllers.UsersCarsController
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.entities.UsersCars
import com.yb.rh.entities.UsersCarsDTO
import com.yb.rh.services.UsersCarsService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

class UsersCarsControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var usersCarsService: UsersCarsService
    private lateinit var usersCarsController: UsersCarsController

    private val testCar = Car(
        plateNumber = "123456",
        country = Countries.IL,
        brand = Brands.TESLA,
        model = "Model 3",
        color = Colors.BLACK,
        carLicenseExpireDate = LocalDateTime.now().plusYears(1)
    )

    private val testUser = User(
        firstName = "Test",
        lastName = "User",
        email = "test@test.com",
        pushNotificationToken = "test-token",
        urlPhoto = null
    )

    private val testUsersCars = UsersCars(
        user = testUser,
        car = testCar
    )
    
    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersCarsService = mockk(relaxed = true)
        usersCarsController = UsersCarsController(usersCarsService)
        mockMvc = MockMvcBuilders.standaloneSetup(usersCarsController).build()
    }

    @Test
    fun `test find all users cars`() {
        // Set up mock
        val usersCars = mutableListOf(testUsersCars)
        every { usersCarsService.getAllUsersCars() } returns usersCars

        // Perform request
        mockMvc.perform(get("/api/users-cars/"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].user.firstName").value("Test"))
            .andExpect(jsonPath("$[0].car.plateNumber").value("123456"))
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.getAllUsersCars() }
    }

    @Test
    fun `test find by plate number`() {
        // Set up mock
        val usersCarsDTO = UsersCarsDTO(
            userId = testUser.userId,
            userCar = testCar.plateNumber,
            blockingCar = null,
            blockedCar = null
        )

        every { usersCarsService.getUsersCarsByPlateNumber("123456") } returns Ok(listOf(usersCarsDTO))

        // Perform request
        mockMvc.perform(get("/api/users-cars/by-plate")
            .param("plateNumber", "123456"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.entity[0].userId").value(testUser.userId))
            .andExpect(jsonPath("$.entity[0].userCar").value("123456"))
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.getUsersCarsByPlateNumber("123456") }
    }

    @Test
    fun `test find by user id`() {
        // Set up mock
        val usersCarsDTO = UsersCarsDTO(
            userId = testUser.userId,
            userCar = testCar.plateNumber,
            blockingCar = null,
            blockedCar = null
        )

        every { usersCarsService.getUsersCarsByUserId(1L) } returns Ok(listOf(usersCarsDTO))

        // Perform request
        mockMvc.perform(get("/api/users-cars/by-user")
            .param("userId", "1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.entity[0].userId").value(testUser.userId))
            .andExpect(jsonPath("$.entity[0].userCar").value("123456"))
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.getUsersCarsByUserId(1L) }
    }

    @Test
    fun `test find by user and plate`() {
        // Set up mock
        val usersCarsDTO = UsersCarsDTO(
            userId = testUser.userId,
            userCar = testCar.plateNumber,
            blockingCar = null,
            blockedCar = null
        )

        every { usersCarsService.getUsersCarsByUserAndPlate(1L, "123456") } returns Ok(usersCarsDTO)

        // Perform request
        mockMvc.perform(get("/api/users-cars/by-user-and-plate")
            .param("userId", "1")
            .param("plateNumber", "123456"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(testUser.userId))
            .andExpect(jsonPath("$.userCar").value("123456"))
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.getUsersCarsByUserAndPlate(1L, "123456") }
    }

    @Test
    fun `test update blocked car`() {
        // Set up mock
        every { usersCarsService.updateBlockedCar("654321", "123456", 1L, UserStatus.BLOCKING) } returns Ok(Unit)

        // Perform request
        mockMvc.perform(post("/api/users-cars/update-blocked")
            .param("blockingCarPlate", "654321")
            .param("blockedCarPlate", "123456")
            .param("userId", "1")
            .param("userStatus", UserStatus.BLOCKING.name))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.updateBlockedCar("654321", "123456", 1L, UserStatus.BLOCKING) }
    }

    @Test
    fun `test release car`() {
        // Set up mock
        every { usersCarsService.releaseCar("654321", "123456", 1L, UserStatus.BLOCKING) } returns Ok(Unit)

        // Perform request
        mockMvc.perform(post("/api/users-cars/release-blocked")
            .param("blockingCarPlate", "654321")
            .param("blockedCarPlate", "123456")
            .param("userId", "1")
            .param("userStatus", UserStatus.BLOCKING.name))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.releaseCar("654321", "123456", 1L, UserStatus.BLOCKING) }
    }

    @Test
    fun `test send free me`() {
        // Set up mock
        every { usersCarsService.sendFreeMe("123456") } returns Ok(Unit)

        // Perform request
        mockMvc.perform(post("/api/users-cars/send-need-to-go-notification")
            .param("blockedCar", "123456"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            
        // Verify service was called
        verify(exactly = 1) { usersCarsService.sendFreeMe("123456") }
    }
} 