import com.yb.rh.repositories.CarsRepository
import com.yb.rh.repositories.UsersCarsRepository
import com.yb.rh.repositories.UsersRepository
import com.yb.rh.services.*
import com.yb.rh.services.countryCarJson.CountryCarJson
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
    fun carsRepository() = mockk<CarsRepository>(relaxed = true)

    @Bean
    @Primary
    fun usersRepository() = mockk<UsersRepository>(relaxed = true)

    @Bean
    @Primary
    fun usersCarsRepository() = mockk<UsersCarsRepository>(relaxed = true)

    @Bean
    @Primary
    fun carApiInterface() = mockk<CarApiInterface>(relaxed = true)

    @Bean
    @Primary
    fun countryCarJson() = mockk<CountryCarJson>(relaxed = true)

    @Bean
    @Primary
    fun notificationService() = mockk<NotificationService>(relaxed = true)

    @Bean
    @Primary
    fun carService() = CarService(carsRepository(), usersRepository(), usersCarsRepository(), carApiInterface())

    @Bean
    @Primary
    fun usersService() = UsersService(usersRepository(), usersCarsRepository())

    @Bean
    @Primary
    fun usersCarsService() = UsersCarsService(usersCarsRepository(), carsRepository(), carService(), usersRepository(), notificationService())
} 