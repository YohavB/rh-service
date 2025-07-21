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

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
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
