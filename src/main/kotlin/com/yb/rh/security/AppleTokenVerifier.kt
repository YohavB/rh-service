package com.yb.rh.security

import com.yb.rh.dtos.AppleUserInfoDTO
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class AppleTokenVerifier {
    
    private val logger = KotlinLogging.logger {}
    private val restTemplate = RestTemplate()
    
    @Value("\${apple.client.id}")
    private lateinit var appleClientId: String
    
    @Value("\${apple.team.id}")
    private lateinit var appleTeamId: String
    
    @Value("\${apple.key.id}")
    private lateinit var appleKeyId: String
    
    fun verifyToken(idToken: String): AppleUserInfoDTO {
        return try {
            // Get Apple's public keys
            val keysResponse = restTemplate.getForObject(
                "https://appleid.apple.com/auth/keys",
                AppleKeysResponse::class.java
            )
            
            // Find the key that matches our key ID
            val key = keysResponse?.keys?.find { it.kid == appleKeyId }
                ?: throw RHException("Apple key not found", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
            
            // Build the public key
            val publicKey = buildPublicKey(key)
            
            // Verify the JWT token
            val claims = verifyJwtToken(idToken, publicKey)
            
            // Validate claims
            validateClaims(claims)
            
            // Extract user info
            AppleUserInfoDTO(
                sub = claims["sub"] as String? ?: "",
                email = claims["email"] as String?,
                name = claims["name"] as String?
            )
        } catch (ex: RHException) {
            throw ex
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to verify Apple token" }
            throw RHException("Failed to verify Apple token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION, throwable = ex)
        }
    }
    
    private fun buildPublicKey(key: AppleKey): PublicKey {
        val modulus = BigInteger(1, Base64.getUrlDecoder().decode(key.n))
        val exponent = BigInteger(1, Base64.getUrlDecoder().decode(key.e))
        
        val keySpec = RSAPublicKeySpec(modulus, exponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        
        return keyFactory.generatePublic(keySpec)
    }
    
    private fun verifyJwtToken(token: String, publicKey: PublicKey): Map<String, Any> {
        // This is a simplified JWT verification
        // In production, you'd use a proper JWT library
        // For now, we'll return a mock implementation
        return mapOf(
            "sub" to "apple_user_id",
            "email" to "user@example.com",
            "name" to "Apple User",
            "email_verified" to "true"
        )
    }
    
    private fun validateClaims(claims: Map<String, Any>) {
        val aud = claims["aud"] as String?
        val iss = claims["iss"] as String?
        
        if (aud != appleClientId) {
            throw RHException("Invalid audience in Apple token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
        }
        
        if (iss != "https://appleid.apple.com") {
            throw RHException("Invalid issuer in Apple token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
        }
    }
}



data class AppleKeysResponse(
    val keys: List<AppleKey>?
)

data class AppleKey(
    val kty: String?,
    val kid: String?,
    val use: String?,
    val alg: String?,
    val n: String?,
    val e: String?
) 