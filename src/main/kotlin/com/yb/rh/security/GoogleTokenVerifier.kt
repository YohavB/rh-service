package com.yb.rh.security

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
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
    
    fun verifyToken(idToken: String): GoogleUserInfo {
        return try {
            val googleIdToken = verifier.verify(idToken)
            
            if (googleIdToken != null) {
                val payload = googleIdToken.payload
                
                // Verify the token is not expired
                if (payload.expirationTimeSeconds * 1000 < System.currentTimeMillis()) {
                    throw RHException("Google ID token has expired", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
                }
                
                // Extract user information
                val email = payload.email
                val emailVerified = payload.emailVerified ?: false
                val name = payload["name"] as String?
                val givenName = payload["given_name"] as String?
                val familyName = payload["family_name"] as String?
                val picture = payload["picture"] as String?
                
                if (email == null || !emailVerified) {
                    throw RHException("Invalid Google ID token: email not verified", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
                }
                
                GoogleUserInfo(
                    email = email,
                    name = name,
                    givenName = givenName,
                    familyName = familyName,
                    picture = picture,
                    emailVerified = emailVerified
                )
            } else {
                throw RHException("Invalid Google ID token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
            }
        } catch (ex: RHException) {
            throw ex
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to verify Google ID token" }
            throw RHException("Failed to verify Google ID token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION, throwable = ex)
        }
    }
}

data class GoogleUserInfo(
    val email: String,
    val name: String?,
    val givenName: String?,
    val familyName: String?,
    val picture: String?,
    val emailVerified: Boolean
) 