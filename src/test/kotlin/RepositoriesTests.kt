import com.github.michaelbull.result.Ok
import com.yb.rh.entities.User
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.repositories.findByEmailSafe
import com.yb.rh.repositories.findByUserIdSafe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RepositoriesTests {
    
    private lateinit var usersRepository: UsersRepository
    
    private val testUser = User(
        firstName = "Yohav", 
        lastName = "Beno", 
        email = "mail@gmail.com", 
        pushNotificationToken = "054318465154", 
        urlPhoto = null,
        userId = 1
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        usersRepository = mockk(relaxed = true)
    }

    @Test
    fun `When findByUserId then return User`() {
        // Set up mock
        every { usersRepository.findByUserId(1) } returns testUser
        
        // Execute the repository function
        val result = usersRepository.findByUserIdSafe(1)
        
        // Verify the result
        assertThat(result).isEqualTo(Ok(testUser))
        
        // Verify the repository method was called
        verify(exactly = 1) { usersRepository.findByUserId(1) }
    }
    
    @Test
    fun `When findByEmail then return User`() {
        // Set up mock
        every { usersRepository.findByEmail("mail@gmail.com") } returns testUser
        
        // Execute the repository function
        val result = usersRepository.findByEmailSafe("mail@gmail.com")
        
        // Verify the result
        assertThat(result).isEqualTo(Ok(testUser))
        
        // Verify the repository method was called
        verify(exactly = 1) { usersRepository.findByEmail("mail@gmail.com") }
    }
}
