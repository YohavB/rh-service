import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("plugin.allopen") version "1.4.32"
    kotlin("plugin.serialization") version "1.6.10"
    id("jacoco")
    id("org.flywaydb.flyway") version "8.5.13"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot", "spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils", "kotlin-logging", "1.5.4")

    implementation("com.michael-bull.kotlin-result", "kotlin-result", "1.1.9")

    // Flyway for database migrations
    implementation("org.flywaydb:flyway-core")

    // retrofit
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2", "retrofit", retrofitVersion)
    implementation("com.squareup.retrofit2", "converter-jackson", retrofitVersion)

    // okhttp
    val okhttpVersion = "4.1.1"
    implementation("com.squareup.okhttp3", "okhttp", okhttpVersion)
    implementation("com.squareup.okhttp3", "logging-interceptor", okhttpVersion)

    //jackson
    val jacksonVersion = "2.11.1"
    implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    implementation("com.fasterxml.jackson.core", "jackson-databind", jacksonVersion)
    implementation("com.fasterxml.jackson.core", "jackson-annotations", jacksonVersion)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-csv", jacksonVersion)

    //moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")

    // GSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    //expo-server-sdk
    implementation ("io.github.jav:expo-server-sdk:1.1.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("net.bytebuddy:byte-buddy:1.14.12")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.12.0")
    
    // TestContainers for integration testing
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation("org.postgresql:postgresql:42.5.4")
    
    // Mockito for integration test mocks
    testImplementation("org.mockito:mockito-core:4.8.1")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
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
    toolVersion = "0.8.9"
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

// Flyway configuration
flyway {
    url = "jdbc:h2:file:./data/myDB"
    user = "root"
    password = "password"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}
