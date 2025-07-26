package com.yb.rh.security

import com.yb.rh.entities.User
import com.yb.rh.repositories.UserRepository
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    private val logger = KotlinLogging.logger {}
    
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")
        
        return createUserDetails(user)
    }
    
    fun loadUserById(userId: Long): UserDetails {
        val user = userRepository.findByUserId(userId)
            ?: throw UsernameNotFoundException("User not found with ID: $userId")
        
        return createUserDetails(user)
    }
    
    private fun createUserDetails(user: User): UserDetails {
        val authorities = mutableListOf<SimpleGrantedAuthority>()
        
        // Add basic user role
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        
        // Add additional roles based on user status
        if (user.isActive) {
            authorities.add(SimpleGrantedAuthority("ROLE_ACTIVE_USER"))
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password("") // No password for OAuth2 users
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(!user.isActive)
            .credentialsExpired(false)
            .disabled(!user.isActive)
            .build()
    }
} 