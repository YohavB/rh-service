import com.yb.rh.RhServiceApplication
import com.yb.rh.RhServiceConfiguration
import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.boot.ApplicationRunner
import kotlin.test.assertNotNull

/**
 * Simple test for the main application class and configuration
 */
class ApplicationTest {

    @Test
    fun `RhServiceApplication can be constructed`() {
        // Simple test to create an instance
        val application = RhServiceApplication()
        assertNotNull(application)
    }
    
    @Test
    fun `RhServiceConfiguration can be constructed`() {
        // Create configuration
        val configuration = RhServiceConfiguration()
        assertNotNull(configuration)
    }
    
    @Test
    fun `RhServiceConfiguration creates a database initializer`() {
        // Create mocks
        val usersRepository = mockk<UsersRepository>(relaxed = true)
        val carsRepository = mockk<CarsRepository>(relaxed = true)
        val usersCarsRepository = mockk<UsersCarsRepository>(relaxed = true)
        
        // Create configuration
        val configuration = RhServiceConfiguration()
        
        // Get the database initializer
        val initializer = configuration.databaseInitializer(
            usersRepository, 
            carsRepository, 
            usersCarsRepository
        )
        
        // Verify it's an ApplicationRunner
        assertNotNull(initializer)
    }
} 