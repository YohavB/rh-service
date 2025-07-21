import com.yb.rh.repositories.CarRepository
import com.yb.rh.repositories.UserCarRepository
import com.yb.rh.repositories.UserRepository
import com.yb.rh.services.*
import com.yb.rh.services.countryCarJson.CountryCarJsonFactory
import io.mockk.mockk
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.test.context.TestPropertySource

@TestConfiguration
@Profile("test")
@EnableConfigurationProperties
@TestPropertySource(properties = ["spring.main.allow-bean-definition-overriding=true"])
class TestConfig {
    @Bean
    @Primary
    fun carsRepository() = mockk<CarRepository>(relaxed = true)

    @Bean
    @Primary
    fun usersRepository() = mockk<UserRepository>(relaxed = true)

    @Bean
    @Primary
    fun usersCarsRepository() = mockk<UserCarRepository>(relaxed = true)

    @Bean
    @Primary
    fun carApiInterface() = mockk<CarApi>(relaxed = true)

    @Bean
    @Primary
    fun countryCarJson() = mockk<CountryCarJsonFactory>(relaxed = true)

    @Bean
    @Primary
    fun notificationService() = mockk<NotificationService>(relaxed = true)

    @Bean
    @Primary
    fun carService() = CarService(carsRepository(), usersRepository(), usersCarsRepository(), carApiInterface())

    @Bean
    @Primary
    fun usersService() = UserService(usersRepository(), usersCarsRepository())

    @Bean
    @Primary
    fun usersCarsService() = UserCarService(usersCarsRepository(), carsRepository(), carService(), usersRepository(), notificationService())
} 