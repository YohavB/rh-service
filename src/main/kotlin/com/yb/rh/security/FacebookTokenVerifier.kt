package com.yb.rh.security

import com.yb.rh.error.RHException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import com.fasterxml.jackson.annotation.JsonProperty

@Component
class FacebookTokenVerifier {
    
    private val logger = KotlinLogging.logger {}
    private val restTemplate = RestTemplate()
    
    @Value("\${facebook.app.id}")
    private lateinit var facebookAppId: String
    
    @Value("\${facebook.app.secret}")
    private lateinit var facebookAppSecret: String
    
    fun verifyToken(accessToken: String): FacebookUserInfo {
        return try {
            // Verify token with Facebook Graph API
            val url = "https://graph.facebook.com/debug_token?" +
                "input_token=$accessToken" +
                "&access_token=${facebookAppId}|${facebookAppSecret}"
            
            val response = restTemplate.getForObject(url, FacebookDebugResponse::class.java)
            
            if (response?.data?.isValid == true && response.data.appId == facebookAppId) {
                // Get user info from Facebook
                val userInfoUrl = "https://graph.facebook.com/me?" +
                    "fields=id,name,email,first_name,last_name,picture" +
                    "&access_token=$accessToken"
                
                val userInfo = restTemplate.getForObject(userInfoUrl, FacebookUserInfo::class.java)
                
                if (userInfo != null && userInfo.email != null) {
                    userInfo
                } else {
                    throw RHException("Failed to get user info from Facebook", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
                }
            } else {
                throw RHException("Invalid Facebook access token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION)
            }
        } catch (ex: RHException) {
            throw ex
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to verify Facebook token" }
            throw RHException("Failed to verify Facebook token", errorType = com.yb.rh.error.ErrorType.AUTHENTICATION, throwable = ex)
        }
    }
}

data class FacebookUserInfo(
    val id: String,
    val name: String?,
    val email: String?,
    @JsonProperty("first_name")
    val firstName: String?,
    @JsonProperty("last_name")
    val lastName: String?,
    val picture: FacebookPicture?
) {
    fun toGoogleUserInfo(): GoogleUserInfo {
        return GoogleUserInfo(
            email = email ?: "",
            name = name,
            givenName = firstName,
            familyName = lastName,
            picture = picture?.data?.url,
            emailVerified = true
        )
    }
}

data class FacebookPicture(
    val data: FacebookPictureData?
)

data class FacebookPictureData(
    val url: String?
)

data class FacebookDebugResponse(
    val data: FacebookDebugData?
)

data class FacebookDebugData(
    @JsonProperty("app_id")
    val appId: String?,
    @JsonProperty("is_valid")
    val isValid: Boolean?
) 