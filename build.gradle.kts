import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("jacoco")
    id("org.flywaydb.flyway") version "9.22.3"
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot", "spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Security dependencies
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // Google OAuth2
    implementation("com.google.api-client:google-api-client:2.0.0") {
        exclude(module = "commons-logging")
    }
    implementation("com.google.auth:google-auth-library-oauth2-http:1.11.0")
    implementation("com.google.auth:google-auth-library-credentials:1.11.0")
    
    // Facebook OAuth2 (using RestTemplate instead of Android SDK)
    // No additional dependencies needed - using Spring's RestTemplate
    
    // Apple Sign In (JWT verification)
    implementation("io.jsonwebtoken:jjwt-api:0.11.5") // Already included above
    
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils", "kotlin-logging", "1.5.4")

    // Flyway for database migrations (used in production profiles)
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("org.flywaydb:flyway-mysql:9.22.3")

    // okhttp - used in CarApi service
    val okhttpVersion = "4.1.1"
    implementation("com.squareup.okhttp3", "okhttp", okhttpVersion)
    implementation("com.squareup.okhttp3", "logging-interceptor", okhttpVersion) {
        exclude(module = "commons-logging")
    }

    // moshi - used in IlCarJson
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    // GSON - used in IlCarJson and GoogleTokenVerifier
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // coroutines - used in NotificationService
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    //expo-server-sdk - used in NotificationService
    implementation ("io.github.jav:expo-server-sdk:1.1.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // MySQL for production
    runtimeOnly("mysql:mysql-connector-java:8.0.33")
    
    // H2 for tests only
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
        exclude(module = "commons-logging") // Exclude commons-logging to fix warning
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.12.0")
    
    // Mockito for integration test mocks
    testImplementation("org.mockito:mockito-core:4.8.1")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.1")
    
    // Security testing
    testImplementation("org.springframework.security:spring-security-test")
}



tasks.withType<Test> {
    useJUnitPlatform()
}

// Configure unit tests to exclude integration tests
tasks.test {
    exclude("**/integration/**")
    exclude("**/IntegrationTestBase*")
    exclude("**/*IntegrationTest*")
    finalizedBy(tasks.jacocoTestReport)
}

// Create a separate task for integration tests
val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    
    // Only include integration tests
    include("**/integration/**")
    include("**/IntegrationTestBase*")
    include("**/*IntegrationTest*")
    
    useJUnitPlatform()
    
    // Set test profile for integration tests
    systemProperty("spring.profiles.active", "test")
    
    // Ensure integration tests run after unit tests
    // dependsOn(tasks.test)
}

// Create a task to run all tests (unit + integration)
val allTests by tasks.registering(Test::class) {
    description = "Runs all tests (unit tests + integration tests)."
    group = "verification"
    
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    
    // Include all tests (no exclusions)
    useJUnitPlatform()
    
    // Set test profile
    systemProperty("spring.profiles.active", "test")
    
    // Run unit tests first, then integration tests
    dependsOn(tasks.test)
    dependsOn(tasks.named("integrationTest"))
    
    // This task will run both test suites
    finalizedBy(tasks.jacocoTestReport)
}

// Task to show test count information
val testCount by tasks.registering {
    description = "Shows the total number of tests in the project."
    group = "verification"
    
    doLast {
        val unitTestCount = fileTree("src/test/kotlin").matching {
            include("**/*Test.kt")
            exclude("**/integration/**")
        }.files.sumOf { file ->
            file.readLines().count { it.contains("@Test") }
        }
        
        val integrationTestCount = fileTree("src/test/kotlin/integration").matching {
            include("**/*Test.kt")
        }.files.sumOf { file ->
            file.readLines().count { it.contains("@Test") }
        }
        
        val totalTests = unitTestCount + integrationTestCount
        
        println("🧪 Test Count Summary:")
        println("   Unit Tests: $unitTestCount methods")
        println("   Integration Tests: $integrationTestCount methods")
        println("   Total Tests: $totalTests methods")
        println("")
        println("📋 Available Test Commands:")
        println("   ./gradlew test          - Run unit tests only")
        println("   ./gradlew integrationTest - Run integration tests only")
        println("   ./gradlew allTests      - Run all tests (recommended)")
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

// Flyway configuration for MySQL (Local Docker Container)
flyway {
    url = "jdbc:mysql://localhost:3306/rh?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    user = "admin"
    password = "root"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}
