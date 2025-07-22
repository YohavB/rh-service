package com.yb.rh.services

import com.yb.rh.TestObjectBuilder
import com.yb.rh.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        userService = UserService(userRepository)
    }

    @Test
    fun `test createUser success`() {
        val userCreationDTO = TestObjectBuilder.getUserCreationDTO()
        val user = mockk<com.yb.rh.entities.User>()
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.save(any()) } returns user
        every { user.toDto() } returns userDTO

        val result = userService.createUser(userCreationDTO)

        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        assertEquals(userDTO.email, result.email)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `test getUserDTOByUserId success`() {
        val userId = 1L
        val user = mockk<com.yb.rh.entities.User>()
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByUserId(userId) } returns user
        every { user.toDto() } returns userDTO

        val result = userService.getUserDTOByUserId(userId)

        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test getUserDTOByUserId not found`() {
        val userId = 1L

        every { userRepository.findByUserId(userId) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.getUserDTOByUserId(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test findUserDTOByEmail success`() {
        val email = "test@example.com"
        val user = mockk<com.yb.rh.entities.User>()
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByEmail(email) } returns user
        every { user.toDto() } returns userDTO

        val result = userService.findUserDTOByEmail(email)

        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test findUserDTOByEmail not found`() {
        val email = "test@example.com"

        every { userRepository.findByEmail(email) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.findUserDTOByEmail(email)
        }
        verify { userRepository.findByEmail(email) }
    }

    @Test
    fun `test getUserById success`() {
        val userId = 1L
        val user = TestObjectBuilder.getUser(userId = userId)

        every { userRepository.findByUserId(userId) } returns user

        val result = userService.getUserById(userId)

        assertNotNull(result)
        assertEquals(user.userId, result.userId)
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test getUserById not found`() {
        val userId = 1L

        every { userRepository.findByUserId(userId) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.getUserById(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test updateUser success`() {
        val userDTO = TestObjectBuilder.getUserDTO()
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)
        val updatedUser = mockk<com.yb.rh.entities.User>(relaxed = true)
        val updatedUserDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByUserId(userDTO.id) } returns user
        every { user.copy(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns updatedUser
        every { userRepository.save(updatedUser) } returns updatedUser
        every { updatedUser.toDto() } returns updatedUserDTO

        val result = userService.updateUser(userDTO)

        assertNotNull(result)
        assertEquals(userDTO.id, result.id)
        verify { userRepository.findByUserId(userDTO.id) }
        verify { userRepository.save(updatedUser) }
    }

    @Test
    fun `test updateUser not found`() {
        val userDTO = TestObjectBuilder.getUserDTO()

        every { userRepository.findByUserId(userDTO.id) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.updateUser(userDTO)
        }
        verify { userRepository.findByUserId(userDTO.id) }
    }

    @Test
    fun `test deActivateUser success`() {
        val userId = 1L
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)

        every { userRepository.findByUserId(userId) } returns user
        every { userRepository.save(user) } returns user

        userService.deActivateUser(userId)

        verify { userRepository.findByUserId(userId) }
        verify { user.isActive = false }
        verify { userRepository.save(user) }
    }

    @Test
    fun `test deActivateUser not found`() {
        val userId = 1L

        every { userRepository.findByUserId(userId) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.deActivateUser(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }

    @Test
    fun `test activateUser success`() {
        val userId = 1L
        val user = mockk<com.yb.rh.entities.User>(relaxed = true)

        every { userRepository.findByUserId(userId) } returns user
        every { userRepository.save(user) } returns user

        userService.activateUser(userId)

        verify { userRepository.findByUserId(userId) }
        verify { user.isActive = true }
        verify { userRepository.save(user) }
    }

    @Test
    fun `test activateUser not found`() {
        val userId = 1L

        every { userRepository.findByUserId(userId) } returns null

        assertThrows<com.yb.rh.error.RHException> {
            userService.activateUser(userId)
        }
        verify { userRepository.findByUserId(userId) }
    }
} 