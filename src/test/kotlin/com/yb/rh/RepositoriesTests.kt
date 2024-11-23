package com.yb.rh

import com.yb.rh.entities.User
import com.yb.rh.repositories.UsersRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class RepositoriesTests @Autowired constructor(
    val entityManager: TestEntityManager,
    val usersRepository: UsersRepository,
) {


    @Test
    fun `When findById then return User`() {
        val yohav = User("Yohav", "Beno", "login", "mail@gmail.com", "054318465154", userId = 1)
        entityManager.persist(yohav)
        entityManager.flush()
        val user = usersRepository.findById(1)
        assertThat(user).isEqualTo(yohav)
    }
}
