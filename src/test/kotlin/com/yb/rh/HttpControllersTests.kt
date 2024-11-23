package com.yb.rh

import com.ninjasquad.springmockk.MockkBean
import com.yb.rh.entities.User
import com.yb.rh.repositories.UsersRepository
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class HttpControllersTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var usersRepository: UsersRepository

    @Test
    fun `List users`() {
        val yohav = User("Yohav", "Beno", "login", "mail@gmail.com", "054318465154", userId = 1)
        val rudy = User("Rudy", "Arrouasse", "login", "mail@gmail.com", "054318465154", userId = 2)
        every { usersRepository.findAll() } returns listOf(yohav, rudy)
        mockMvc.perform(get("/api/users/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].email").value(yohav.email))
            .andExpect(jsonPath("\$.[1].email").value(rudy.email))
    }
}