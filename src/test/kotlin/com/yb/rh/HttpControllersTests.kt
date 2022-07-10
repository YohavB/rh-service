package com.yb.rh

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class HttpControllersTests(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var usersRepository: UsersRepository

    @Test
    fun `List users`() {
        val yohav = Users(1, "Yohav", "Beno", "login","mail@gmail.com", "054318465154")
        val rudy = Users(2, "Rudy", "Arrouasse","login","mail@gmail.com", "054318465154")
        every { usersRepository.findAll() } returns listOf(yohav, rudy)
        mockMvc.perform(get("/api/users/").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.[0].login").value(yohav.login))
            .andExpect(jsonPath("\$.[1].login").value(rudy.login))
    }
}