import com.fasterxml.jackson.databind.ObjectMapper
import com.yb.rh.controllers.UsersController
import com.yb.rh.entities.User
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class HttpControllersTests {

    private lateinit var mockMvc: MockMvc
    private lateinit var usersService: UsersService
    private lateinit var usersController: UsersController
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersService = mockk(relaxed = true)
        objectMapper = ObjectMapper()
        usersController = UsersController(usersService)
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build()
    }

    @Test
    fun `List users`() {
        val yohav = User("Yohav", "Beno", "mail@gmail.com", "054318465154", null, userId = 1)
        val rudy = User("Rudy", "Arrouasse", "mail@gmail.com", "054318465154", null, userId = 2)
        
        every { usersService.findAll() } returns listOf(yohav, rudy)
        
        mockMvc.perform(get("/api/users/").accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].email").value(yohav.email))
            .andExpect(jsonPath("$[1].email").value(rudy.email))
            
        verify(exactly = 1) { usersService.findAll() }
    }
}