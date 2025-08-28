package com.yb.rh.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class FirebaseService {

    private val logger = KotlinLogging.logger {}

    @Value("\${firebase.project-id:rushhour-yohavb}")
    private lateinit var projectId: String

    @Value("\${firebase.private-key-id:}")
    private lateinit var privateKeyId: String

    @Value("\${firebase.private-key:}")
    private lateinit var privateKey: String

    @Value("\${firebase.client-email:}")
    private lateinit var clientEmail: String

    @Value("\${firebase.client-id:}")
    private lateinit var clientId: String

    @Value("\${firebase.auth-uri:https://accounts.google.com/o/oauth2/auth}")
    private lateinit var authUri: String

    @Value("\${firebase.token-uri:https://oauth2.googleapis.com/token}")
    private lateinit var tokenUri: String

    @Value("\${firebase.auth-provider-x509-cert-url:https://www.googleapis.com/oauth2/v1/certs}")
    private lateinit var authProviderX509CertUrl: String

    @Value("\${firebase.client-x509-cert-url:}")
    private lateinit var clientX509CertUrl: String

    private lateinit var firebaseApp: FirebaseApp
    private lateinit var firebaseMessaging: FirebaseMessaging

    @PostConstruct
    fun initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                val credentials = createCredentialsFromEnvironment()
                
                val options = FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build()

                firebaseApp = FirebaseApp.initializeApp(options)
                firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
                
                logger.info { "Firebase Admin SDK initialized successfully for project: $projectId" }
            } else {
                firebaseApp = FirebaseApp.getInstance()
                firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp)
                logger.info { "Firebase Admin SDK already initialized, using existing instance" }
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to initialize Firebase Admin SDK" }
            throw RuntimeException("Firebase initialization failed", e)
        }
    }

    private fun createCredentialsFromEnvironment(): GoogleCredentials {
        // Check if we have the minimum required environment variables
        if (privateKey.isBlank() || clientEmail.isBlank() || projectId.isBlank()) {
            throw IllegalStateException(
                "Missing required Firebase environment variables. " +
                "Required: FIREBASE_PRIVATE_KEY, FIREBASE_CLIENT_EMAIL, FIREBASE_PROJECT_ID"
            )
        }

        // Create the service account JSON structure from environment variables
        val serviceAccountJson = """
        {
            "type": "service_account",
            "project_id": "$projectId",
            "private_key_id": "$privateKeyId",
            "private_key": "$privateKey",
            "client_email": "$clientEmail",
            "client_id": "$clientId",
            "auth_uri": "$authUri",
            "token_uri": "$tokenUri",
            "auth_provider_x509_cert_url": "$authProviderX509CertUrl",
            "client_x509_cert_url": "$clientX509CertUrl"
        }
        """.trimIndent()

        return try {
            GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray()))
        } catch (e: IOException) {
            logger.error(e) { "Failed to create Google credentials from environment variables" }
            throw RuntimeException("Failed to create Firebase credentials", e)
        }
    }

    /**
     * Send a notification to a specific FCM token
     */
    fun sendNotificationToToken(
        token: String,
        title: String,
        body: String,
        sound: String,
        data: Map<String, String> = emptyMap()
    ): String? {
        return try {
            val message = createMessage(token = token, title = title, body = body, sound = sound, data = data)

            val response = firebaseMessaging.send(message)
            logger.info { "Successfully sent message to token ${token.subSequence(0,9)} - response = $response" }
            response
        } catch (e: Exception) {
            logger.error(e) { "Failed to send notification to token ${token.subSequence(0,9)}" }
            null
        }
    }

    /**
     * Send a notification to a topic
     */
    fun sendNotificationToTopic(
        topic: String,
        title: String,
        body: String,
        sound: String?,
        data: Map<String, String> = emptyMap()
    ): String? {
        return try {
            val message = createMessage(title = title, body = body, sound = sound, data = data)

            val response = firebaseMessaging.send(message)
            logger.info { "Successfully sent message to topic $topic: $response" }
            response
        } catch (e: Exception) {
            logger.error(e) { "Failed to send notification to topic $topic" }
            null
        }
    }

    /**
     * Subscribe a token to a topic
     */
    fun subscribeToTopic(tokens: List<String>, topic: String): Boolean {
        return try {
            val response: TopicManagementResponse = firebaseMessaging.subscribeToTopic(tokens, topic)
            logger.info { "Successfully subscribed ${response.successCount} tokens to topic $topic" }
            if (response.failureCount > 0) {
                logger.warn { "Failed to subscribe ${response.failureCount} tokens to topic $topic" }
            }
            response.successCount > 0
        } catch (e: Exception) {
            logger.error(e) { "Failed to subscribe tokens to topic $topic" }
            false
        }
    }

    /**
     * Unsubscribe a token from a topic
     */
    fun unsubscribeFromTopic(tokens: List<String>, topic: String): Boolean {
        return try {
            val response: TopicManagementResponse = firebaseMessaging.unsubscribeFromTopic(tokens, topic)
            logger.info { "Successfully unsubscribed ${response.successCount} tokens from topic $topic" }
            if (response.failureCount > 0) {
                logger.warn { "Failed to unsubscribe ${response.failureCount} tokens from topic $topic" }
            }
            response.successCount > 0
        } catch (e: Exception) {
            logger.error(e) { "Failed to unsubscribe tokens from topic $topic" }
            false
        }
    }

    /**
     * Validate if a token is valid by attempting to send a test message
     */
    fun isTokenValid(token: String): Boolean {
        return try {
            val message = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle("Test")
                        .setBody("Test message")
                        .build()
                )
                .build()

            firebaseMessaging.send(message)
            true
        } catch (e: Exception) {
            logger.debug { "Token validation failed for ${token.subSequence(0,9)}: ${e.message}" }
            false
        }
    }

    private fun createMessage(
        title: String,
        body: String,
        token: String? = null,
        sound: String? = null,
        data: Map<String, String> = emptyMap()
    ): Message? {
        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build()
            )
            .putAllData(data)

        if (token != null) message.setToken(token)

        if (sound != null) {
            message.setAndroidConfig(
                AndroidConfig.builder()
                    .setNotification(
                        AndroidNotification.builder()
                            .setSound(sound.removeSuffix(".wav"))
                            .build()
                    )
                    .build()
            )

            message.setApnsConfig(
                ApnsConfig.builder()
                    .setAps(
                        Aps.builder()
                            .setSound(sound)
                            .build()
                    )
                    .build()
            )
        }
        return message.build()
    }

    /**
     * Get Firebase Messaging instance
     */
    fun getFirebaseMessaging(): FirebaseMessaging {
        return firebaseMessaging
    }
}