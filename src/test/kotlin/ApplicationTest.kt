import com.yb.rh.RhServiceApplication
import com.yb.rh.RhServiceConfiguration
import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import io.mockk.mockk
import org.junit.jupiter.api.Test
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
        val userRepository = mockk<UserRepository>(relaxed = true)
        val carRepository = mockk<CarRepository>(relaxed = true)
        val userCarRepository = mockk<UserCarRepository>(relaxed = true)
        
        // Create configuration
        val configuration = RhServiceConfiguration()
        
        // Get the database initializer
        val initializer = configuration.databaseInitializer(
            userRepository,
            carRepository,
            userCarRepository
        )
        
        // Verify it's an ApplicationRunner
        assertNotNull(initializer)
    }
} 