package com.yb.rh.services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class FirebaseService {

    private val logger = KotlinLogging.logger {}

    @Value("\${firebase.service-account.path:rushhour-firebase-adminsdk.json}")
    private lateinit var serviceAccountPath: String

    @Value("\${firebase.project-id:rushhour-yohavb}")
    private lateinit var projectId: String

    private lateinit var firebaseApp: FirebaseApp
    private lateinit var firebaseMessaging: FirebaseMessaging

    @PostConstruct
    fun initializeFirebase() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                val serviceAccount = ClassPathResource(serviceAccountPath).inputStream
                
                val options = FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
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
        } catch (e: IOException) {
            logger.error(e) { "Failed to read Firebase service account file: $serviceAccountPath" }
            throw RuntimeException("Firebase initialization failed", e)
        } catch (e: Exception) {
            logger.error(e) { "Failed to initialize Firebase Admin SDK" }
            throw RuntimeException("Firebase initialization failed", e)
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