package com.yb.rh.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.crypto.SecretKey
import javax.xml.bind.DatatypeConverter

@Component
class JwtTokenProvider {
    
    private val logger = KotlinLogging.logger {}
    
    @Value("\${jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private lateinit var jwtSecret: String
    
    @Value("\${jwt.expiration:86400000}") // 24 hours in milliseconds
    private var jwtExpiration: Long = 86400000
    
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }
    
    fun generateToken(userId: Long, email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)
        
        return Jwts.builder()
            .setSubject(userId.toString())
            .claim("email", email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun getUserIdFromToken(token: String): Long? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims.subject.toLong()
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to extract user ID from token" }
            null
        }
    }
    
    fun getEmailFromToken(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
            
            claims["email"] as String?
        } catch (ex: Exception) {
            logger.warn(ex) { "Failed to extract email from token" }
            null
        }
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: SignatureException) {
            logger.warn(ex) { "Invalid JWT signature" }
            false
        } catch (ex: MalformedJwtException) {
            logger.warn(ex) { "Invalid JWT token" }
            false
        } catch (ex: ExpiredJwtException) {
            logger.warn(ex) { "Expired JWT token" }
            false
        } catch (ex: UnsupportedJwtException) {
            logger.warn(ex) { "Unsupported JWT token" }
            false
        } catch (ex: IllegalArgumentException) {
            logger.warn(ex) { "JWT claims string is empty" }
            false
        }
    }
} 