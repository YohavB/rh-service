package com.yb.rh.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.yb.rh.dtos.GoogleUserInfoDTO
import com.yb.rh.error.ErrorType
import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GoogleTokenVerifier {
    
    private val logger = KotlinLogging.logger {}
    
    @Value("\${google.client.id}")
    private lateinit var googleClientId: String
    
    private val verifier: GoogleIdTokenVerifier by lazy {
        GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(googleClientId))
            .build()
    }
    
    fun verifyToken(idToken: String): GoogleUserInfoDTO {
        return try {
            logger.info { "Verifying Google ID token: ${idToken.take(20)}..." }
            val googleIdToken = verifier.verify(idToken)
            
            if (googleIdToken != null) {
                val payload = googleIdToken.payload
                
                if (payload.expirationTimeSeconds * 1000 < System.currentTimeMillis()) {
                    throw RHException("Google ID token has expired", ErrorType.AUTHENTICATION)
                }
                
                val email = payload.email
                val emailVerified = payload.emailVerified
                val name = payload["name"] as String?
                val givenName = payload["given_name"] as String?
                val familyName = payload["family_name"] as String?
                val picture = payload["picture"] as String?
                
                if (email == null || !emailVerified) {
                    throw RHException("Invalid Google ID token: email not verified", ErrorType.AUTHENTICATION)
                }

                GoogleUserInfoDTO(
                    email = email,
                    name = name,
                    givenName = givenName,
                    familyName = familyName,
                    picture = picture
                )
            } else {
                throw RHException("Invalid Google ID token", ErrorType.AUTHENTICATION)
            }
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to verify Google ID token" }
            throw RHException("Failed to verify Google ID token", ErrorType.AUTHENTICATION, ex)
        }
    }
}

 