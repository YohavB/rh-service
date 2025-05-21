import com.github.michaelbull.result.Ok
import com.yb.rh.common.Brands
import com.yb.rh.common.Colors
import com.yb.rh.common.UserStatus
import com.yb.rh.entities.Car
import com.yb.rh.entities.User
import com.yb.rh.services.CarService
import com.yb.rh.services.NotificationService
import com.yb.rh.services.UsersCarsService
import com.yb.rh.services.UsersService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ServiceIntegrationTest {

    private lateinit var carService: CarService
    private lateinit var usersService: UsersService
    private lateinit var usersCarsService: UsersCarsService
    private lateinit var notificationService: NotificationService

    @BeforeEach
    fun setup() {
        clearAllMocks()
        carService = mockk(relaxed = true)
        usersService = mockk(relaxed = true)
        notificationService = mockk(relaxed = true)
        usersCarsService = mockk(relaxed = true)
    }

    @Test
    fun `test complete car blocking flow`() {
        // Setup test data
        val blockingCar = Car(
            plateNumber = "123456",
            brand = Brands.TESLA,
            model = "Model 3",
            color = Colors.BLACK,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )

        val blockedCar = Car(
            plateNumber = "654321",
            brand = Brands.BMW,
            model = "X5",
            color = Colors.WHITE,
            carLicenseExpireDate = LocalDateTime.now().plusYears(1)
        )

        val user = User(
            firstName = "Test",
            lastName = "User",
            email = "test@test.com",
            pushNotificationToken = "test-token",
            urlPhoto = null
        )

        // Mock service responses
        every { carService.createOrUpdateCar(blockingCar.plateNumber, user.userId) } returns Ok(blockingCar)
        every { usersCarsService.updateBlockedCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        ) } returns Ok(Unit)
        every { usersCarsService.sendFreeMe(blockedCar.plateNumber) } returns Ok(Unit)
        every { usersCarsService.releaseCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        ) } returns Ok(Unit)

        // Test the complete flow
        val createCarResult = carService.createOrUpdateCar(blockingCar.plateNumber, user.userId)
        assertNotNull(createCarResult)
        assertEquals(Ok(blockingCar), createCarResult)

        val blockResult = usersCarsService.updateBlockedCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        )
        assertNotNull(blockResult)
        assertEquals(Ok(Unit), blockResult)

        val freeMeResult = usersCarsService.sendFreeMe(blockedCar.plateNumber)
        assertNotNull(freeMeResult)
        assertEquals(Ok(Unit), freeMeResult)

        val releaseResult = usersCarsService.releaseCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        )
        assertNotNull(releaseResult)
        assertEquals(Ok(Unit), releaseResult)

        // Verify service calls
        verify(exactly = 1) { carService.createOrUpdateCar(blockingCar.plateNumber, user.userId) }
        verify(exactly = 1) { usersCarsService.updateBlockedCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        ) }
        verify(exactly = 1) { usersCarsService.sendFreeMe(blockedCar.plateNumber) }
        verify(exactly = 1) { usersCarsService.releaseCar(
            blockingCar.plateNumber,
            blockedCar.plateNumber,
            user.userId,
            UserStatus.BLOCKING
        ) }
    }
} 