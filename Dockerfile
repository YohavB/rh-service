# Multi-stage build for Kotlin Spring Boot application
# Build stage with environment-specific configuration
FROM gradle:8.5-jdk17 AS build
ARG BUILD_ENV=prod

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle.kts settings.gradle.kts ./

# Download dependencies (this layer will be cached if dependencies don't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Copy environment-specific configuration
COPY env.${BUILD_ENV} .env

# Build the application with environment-specific profile
RUN gradle build --no-daemon -x test -Dspring.profiles.active=${BUILD_ENV}

# Runtime stage
FROM eclipse-temurin:17-jre
ARG BUILD_ENV=prod

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Copy environment configuration
COPY env.${BUILD_ENV} .env

# Create directory for Firebase service account file
RUN mkdir -p /app/config

# Change ownership to app user
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose the application port
EXPOSE 8008

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
CMD curl -f http://localhost:8008/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# Set build environment for runtime
ENV BUILD_ENV=${BUILD_ENV}

# Run the application with environment-specific profile
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${BUILD_ENV}"]