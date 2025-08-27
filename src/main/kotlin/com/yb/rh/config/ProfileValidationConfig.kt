package com.yb.rh.config

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Profile validation configuration that ensures a specific profile is always set.
 * This prevents accidentally running the application without a profile.
 */
@Component
class ProfileValidationConfig(private val environment: Environment) {

    @PostConstruct
    fun validateActiveProfile() {
        val activeProfiles = environment.activeProfiles
        
        if (activeProfiles.isEmpty()) {
            throw IllegalStateException("""
                ❌ NO ACTIVE PROFILE SET!
                
                You must explicitly set a Spring profile to run the application.
                
                Available profiles:
                - local: Local development with Coolify database
                - prod: Production environment with Coolify database
                - test: Testing environment with H2 database
                
                How to set a profile:
                
                1. Command line:
                   ./gradlew bootRun --args='--spring.profiles.active=local'
                
                2. Environment variable:
                   export SPRING_PROFILES_ACTIVE=local
                   ./gradlew bootRun
                
                3. IDE VM options:
                   -Dspring.profiles.active=local
                
                4. Docker:
                   BUILD_ENV=local docker-compose up --build
                
                ❌ Application startup aborted for safety.
            """.trimIndent())
        }
        
        // Validate that the profile is one of the expected ones
        val validProfiles = setOf("local", "prod", "test", "integration-test")
        val invalidProfiles = activeProfiles.filter { it !in validProfiles }
        
        if (invalidProfiles.isNotEmpty()) {
            throw IllegalStateException("""
                ❌ INVALID PROFILE(S) DETECTED: ${invalidProfiles.joinToString(", ")}
                
                Valid profiles are: ${validProfiles.joinToString(", ")}
                
                Current active profiles: ${activeProfiles.joinToString(", ")}
                
                ❌ Application startup aborted for safety.
            """.trimIndent())
        }
        
        println("✅ Active profile(s): ${activeProfiles.joinToString(", ")}")
    }
}
