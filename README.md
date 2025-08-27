# 🚗 RushHour Backend (RH-BE)

A modern Spring Boot backend application for the RushHour service, built with Kotlin and designed for production deployment with Docker support.

## 🌟 **Overview**

RushHour Backend is a RESTful API service that manages users, cars, and their relationships. The application uses MySQL as its primary database and is designed to work with external database services like Coolify.

## ✨ **Features**

- ✅ **Spring Boot 3.2.0** with Kotlin 1.9.22
- ✅ **Java 17** runtime with optimized JVM settings
- ✅ **MySQL Database** with Flyway migrations
- ✅ **OAuth2 Authentication** (Google, Apple, Facebook)
- ✅ **JWT Token Management** with secure session handling
- ✅ **RESTful APIs** for user and car management
- ✅ **Push Notifications** via Firebase
- ✅ **Comprehensive Testing** with high coverage
- ✅ **Production Ready** with proper error handling and logging
- ✅ **Environment Profiles** for different deployment scenarios
- ✅ **Docker Support** with multi-stage builds
- ✅ **External Database Service** integration (Coolify)

## 🚀 **Quick Start**

### **Prerequisites**

- **Java 17** or higher
- **Docker** (for containerized deployment)
- **Gradle** (included via wrapper)
- **Coolify** database (or local MySQL for development)

### **Option 1: Docker Deployment (Recommended)**

#### **1. Build and Run with Docker Compose**

```bash
# Clone the repository
git clone <your-repo-url>
cd RH-BE

# Build and run local environment
BUILD_ENV=local docker-compose up --build -d

# Or build and run production environment
BUILD_ENV=prod docker-compose up --build -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f backend
```

#### **2. Using Makefile (Easier)**

```bash
# Show all available commands
make help

# Local development environment
make dev-setup

# Production environment
make prod-setup

# Stop all services
make stop

# View logs
make logs
```

### **Option 2: Local Development**

#### **1. Set Environment Variables**

```bash
# Copy environment template
cp env.local .env

# Or for production
cp env.prod .env
```

#### **2. Run with Gradle**

```bash
# Local development
./gradlew bootRun --args='--spring.profiles.active=local'

# Production
./gradlew bootRun --args='--spring.profiles.active=prod'

# Test
./gradlew bootRun --args='--spring.profiles.active=test'
```

#### **3. Using Environment Variables**

```bash
# Set profile
export SPRING_PROFILES_ACTIVE=local

# Run application
./gradlew bootRun
```

## 🎯 **Environment Profiles**

The application **REQUIRES** an explicit Spring profile to run. This is a security feature to prevent accidentally running with the wrong configuration.

### **Available Profiles**

| Profile | Database | Purpose | Flyway |
|---------|----------|---------|---------|
| **`local`** | Coolify database | Local development | Disabled |
| **`prod`** | Coolify database | Production environment | Enabled |
| **`test`** | H2 in-memory | Unit testing | Disabled |
| **`integration-test`** | H2 in-memory | Integration testing | Disabled |

### **Profile Validation**

The application includes automatic profile validation that will:
- ✅ Check if any profile is active
- ✅ Validate profile names (only allow: local, prod, test, integration-test)
- ✅ Fail fast with clear error messages if validation fails

## 🔧 **IDE Configuration**

### **IntelliJ IDEA / Android Studio**

1. **Edit Configurations** → **+** → **Application**
2. **Main class**: `com.yb.rh.RhServiceApplication`
3. **VM options**: `-Dspring.profiles.active=local`
4. **Program arguments**: (leave empty)
5. **Working directory**: `$MODULE_WORKING_DIR$`
6. **Use classpath of module**: `rh-service.main`

### **VS Code**

Create `.vscode/launch.json`:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "RushHour Backend - Local",
            "request": "launch",
            "mainClass": "com.yb.rh.RhServiceApplication",
            "projectName": "rh-service",
            "vmArgs": "-Dspring.profiles.active=local"
        }
    ]
}
```

### **Eclipse**

1. **Run** → **Run Configurations** → **Java Application**
2. **Main class**: `com.yb.rh.RhServiceApplication`
3. **Arguments** → **VM arguments**: `-Dspring.profiles.active=local`

## 🐳 **Docker Configuration**

### **Dockerfile Features**

- **Multi-stage build** for optimized image size
- **Java 17** runtime (matches your build configuration)
- **Security-focused** with non-root user
- **Health checks** for monitoring
- **Production-optimized** JVM settings

### **Environment-Specific Builds**

```bash
# Build local environment
./build-docker.sh

# Build production environment
./build-docker.sh -e prod

# Build with custom tag
./build-docker.sh -e prod -t v1.0.0

# Build and push to registry
./build-docker.sh -e prod -p
```

### **Docker Compose Services**

- **Backend Service**: Port 8008, environment-aware configuration
- **Database**: Uses Coolify database (no local MySQL container)
- **Networks**: Isolated Docker network for security

## 🗄️ **Database Configuration**

### **Coolify Database (Production)**

```bash
# Database connection
DB_URL=jdbc:mysql://u08w4kcg4sows0s0ksokcg4s:3306/default?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=user
DB_PASSWORD=your-secure-password
```

### **Local Development (Optional)**

```bash
# Local database fallback
DB_URL=jdbc:mysql://localhost:3306/default?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=admin
DB_PASSWORD=root
```

## 🔄 **Flyway Database Migrations**

### **Environment-Aware Configuration**

The Flyway configuration automatically uses the appropriate database settings based on your environment:

```kotlin
flyway {
    url = System.getenv("DB_URL") ?: "jdbc:mysql://localhost:3306/default?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    user = System.getenv("DB_USERNAME") ?: "admin"
    password = System.getenv("DB_PASSWORD") ?: "root"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}
```

### **Available Flyway Tasks**

```bash
# Standard tasks
./gradlew flywayMigrate
./gradlew flywayInfo
./gradlew flywayValidate

# Environment-specific tasks
./gradlew flywayMigrateLocal
./gradlew flywayMigrateProd
./gradlew flywayInfoLocal
./gradlew flywayInfoProd

# Using Makefile
make migrate-local
make migrate-prod
make migrate-info-local
make migrate-info-prod
```

### **Migration Workflow**

- **Local Environment**: Migrations run manually via `make migrate-local`
- **Production Environment**: Migrations run automatically on startup
- **Migration Files**: Located in `src/main/resources/db/migration/`

## 🔐 **Security & Authentication**

### **OAuth2 Providers**

- **Google Sign-In** - OAuth2 with ID token verification
- **Apple Sign-In** - JWT token verification
- **Facebook Login** - Access token verification

### **JWT Token Management**

- **Token Expiration**: 24 hours
- **Refresh**: Available via `/api/v1/auth/refresh`
- **Header**: `Authorization: Bearer <jwt_token>`

### **User Consent Flow**

The API implements a consent-based authentication flow for new users:

1. **New User First Login**: User attempts OAuth login without consent parameter
2. **Consent Required Error**: API returns `403 Forbidden` with `USER_CONSENT_REQUIRED` error
3. **Client Shows Agreement**: Frontend displays consent agreement to user
4. **User Agrees**: User reads and accepts the terms
5. **Second Login**: Client calls OAuth endpoint again with `agreedConsent=true`
6. **Success**: User is created and authenticated successfully

## 📡 **API Endpoints**

### **Authentication**
- `POST /api/v1/auth/google` - Google OAuth2 login
- `POST /api/v1/auth/facebook` - Facebook Login
- `POST /api/v1/auth/apple` - Apple Sign In
- `POST /api/v1/auth/refresh` - Refresh JWT token
- `POST /api/v1/auth/logout` - Logout (client-side)

### **Users**
- `POST /api/v1/user` - Create user
- `GET /api/v1/user?id={id}` - Get user by ID
- `GET /api/v1/user/by-email?email={email}` - Get user by email
- `PUT /api/v1/user` - Update user
- `PUT /api/v1/user/deactivate/{userId}` - Deactivate user
- `PUT /api/v1/user/activate/{userId}` - Activate user

### **Cars**
- `POST /api/v1/car` - Create car
- `GET /api/v1/car?id={id}` - Get car by ID
- `GET /api/v1/car/by-plate?plateNumber={plate}` - Get car by plate number
- `PUT /api/v1/car` - Update car
- `DELETE /api/v1/car/{id}` - Delete car

### **User-Car Relationships**
- `POST /api/v1/user-car` - Assign car to user
- `GET /api/v1/user-car?userId={id}` - Get user's cars
- `DELETE /api/v1/user-car` - Remove car from user

### **Car Relations (Blocking)**
- `POST /api/v1/car-relations` - Create blocking relationship
- `GET /api/v1/car-relations?carId={id}` - Get car's blocking relationships
- `DELETE /api/v1/car-relations` - Remove blocking relationship

### **Notifications**
- `POST /api/v1/notification` - Send push notification

### **Health Check**
- `GET /health` - Application health status

## 🛠️ **Available Commands**

### **Application Management**
```bash
# Local development
make dev-setup

# Production environment
make prod-setup

# Stop all services
make stop

# View logs
make logs
```

### **Database Management**
```bash
# Run migrations
make migrate-local
make migrate-prod

# Check migration status
make migrate-info-local
make migrate-info-prod

# Database backup (Coolify managed)
make backup
```

### **Testing**
```bash
# Run all tests
./gradlew allTests

# Unit tests only
./gradlew test

# Integration tests only
./gradlew integrationTest

# Test statistics
./gradlew testCount
```

### **Build & Deployment**
```bash
# Build application
./gradlew build

# Build Docker image
./build-docker.sh -e local
./build-docker.sh -e prod

# Run with Gradle
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 📁 **Project Structure**

```
RH-BE/
├── src/
│   ├── main/kotlin/com/yb/rh/
│   │   ├── controllers/     # REST API controllers
│   │   ├── services/        # Business logic
│   │   ├── repositories/    # Data access layer
│   │   ├── entities/        # JPA entities
│   │   ├── dtos/           # Data transfer objects
│   │   ├── error/          # Error handling
│   │   ├── security/       # Authentication & authorization
│   │   ├── config/         # Configuration classes
│   │   └── utils/          # Utility classes
│   ├── main/resources/
│   │   ├── db/migration/   # Flyway migration scripts
│   │   └── application*.properties  # Configuration profiles
│   └── test/               # Test suites
├── build.gradle.kts        # Build configuration
├── Dockerfile              # Multi-stage Docker build
├── docker-compose.yml      # Service orchestration
├── Makefile                # Development commands
├── build-docker.sh         # Docker build script
├── env.local               # Local environment config
├── env.prod                # Production environment config
└── README.md               # This file
```

## 🔧 **Configuration**

### **Environment Variables**

```bash
# Database Configuration
DB_URL=jdbc:mysql://host:port/database?params
DB_USERNAME=username
DB_PASSWORD=password

# JWT Configuration
JWT_SECRET=your-secure-jwt-secret
JWT_EXPIRATION=86400000

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_APP_SECRET=your-facebook-app-secret
APPLE_CLIENT_ID=your-apple-client-id
APPLE_TEAM_ID=your-apple-team-id
APPLE_KEY_ID=your-apple-key-id

# Firebase Configuration
FIREBASE_PROJECT_ID=your-firebase-project-id
```

### **Profile-Specific Settings**

#### **Local Profile (`local`)**
- **Database**: Coolify database
- **Flyway**: Disabled by default
- **Logging**: Verbose
- **Hibernate**: `ddl-auto=validate`

#### **Production Profile (`prod`)**
- **Database**: Coolify database
- **Flyway**: Enabled with validation
- **Logging**: Minimal
- **Hibernate**: `ddl-auto=validate`

#### **Test Profile (`test`)**
- **Database**: H2 in-memory
- **Flyway**: Disabled
- **Logging**: Minimal
- **Hibernate**: `ddl-auto=create-drop`

## 🚨 **Error Handling**

### **Error Response Format**
```json
{
  "cause": "Error description",
  "errorCode": 400
}
```

### **Common Error Types**
- `AUTHENTICATION` - Authentication failures
- `INVALID_INPUT` - Invalid request data
- `ENTITY_NOT_FOUND` - Resource not found
- `RESOURCE_ALREADY_EXISTS` - Duplicate resources
- `USER_CONSENT_REQUIRED` - User consent needed
- `DB_ACCESS` - Database access issues
- `HTTP_CALL` - External service failures

## 🔍 **Monitoring & Health Checks**

### **Health Endpoints**
- **Application**: `GET /health`
- **Database**: Connection monitoring via Flyway
- **Docker**: Container health checks

### **Logging Configuration**
- **Application logs** - INFO level for business operations
- **SQL logs** - WARN level in production
- **Web logs** - WARN level for HTTP requests
- **Structured logging** - JSON format for production

## 🚀 **Production Deployment**

### **1. Environment Setup**
```bash
# Set production environment
export BUILD_ENV=prod

# Configure environment variables
cp env.prod .env
nano .env  # Update with production values
```

### **2. Docker Deployment**
```bash
# Build and run production
BUILD_ENV=prod docker-compose up --build -d

# Or use Makefile
make prod-setup
```

### **3. Health Monitoring**
```bash
# Check service status
make health

# View logs
make logs-backend

# Monitor resources
docker stats
```

## 🧪 **Testing**

### **Test Structure**
- **Unit Tests** - Test individual components
- **Integration Tests** - Test with real database
- **Test Coverage** - JaCoCo reporting configured

### **Running Tests**
```bash
# All tests
./gradlew allTests

# Unit tests only
./gradlew test

# Integration tests only
./gradlew integrationTest

# Test statistics
./gradlew testCount
```

### **Test Configuration**
- Unit tests use H2 in-memory database
- Integration tests use H2 via TestContainers
- Separate test profiles for different scenarios

## 🛡️ **Security Considerations**

### **Current Implementation**
- ✅ Input validation with Spring Validation
- ✅ SQL injection prevention via JPA
- ✅ Error message sanitization
- ✅ Environment-based configuration
- ✅ JWT token-based authentication
- ✅ OAuth2 provider integration
- ✅ CORS configuration

### **Recommended Improvements**
- [ ] Add rate limiting
- [ ] Enable HTTPS/TLS
- [ ] Add API documentation (OpenAPI/Swagger)
- [ ] Implement audit logging
- [ ] Add security headers

## 📈 **Performance Optimization**

### **Database**
- Connection pooling with HikariCP
- Optimized indexes on frequently queried columns
- Query optimization with proper JPA annotations

### **Application**
- Async processing for notifications
- Efficient DTO mapping
- Proper exception handling to prevent memory leaks
- JVM optimization for Docker containers

## 🔍 **Troubleshooting**

### **Common Issues**

#### **1. "No active profile set" Error**
**Cause**: No Spring profile specified
**Solution**: Set `-Dspring.profiles.active=local` in VM options

#### **2. Database Connection Issues**
**Cause**: Wrong profile for database type
**Solution**: 
- **Local/Prod**: Use `local` or `prod` profile
- **Testing**: Use `test` profile

#### **3. Migration Failures**
```bash
# Check migration status
make migrate-info-local

# Verify environment variables
docker-compose exec backend env | grep -E "(DATABASE|FLYWAY)"
```

#### **4. Docker Build Issues**
```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker-compose build --no-cache
```

### **Debug Commands**
```bash
# Check current status
make status
make health

# View environment variables
docker-compose exec backend env

# Check application logs
make logs-backend
```

## 📚 **Additional Documentation**

- **[API SDK Documentation](./API_SDK_DOCUMENTATION.md)** - Complete API reference
- **[Security Implementation](./SECURITY.md)** - Detailed security guide

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 🏗️ **Architecture Decisions**

### **Why Environment Profiles?**
- **Security** - Prevents accidental wrong configuration
- **Flexibility** - Different settings for different environments
- **Consistency** - Same codebase, different configurations

### **Why Docker?**
- **Reproducibility** - Consistent environment across machines
- **Scalability** - Easy to scale and deploy
- **Isolation** - Services run in isolated containers

### **Why Flyway?**
- **Version Control** - Database schema changes are tracked
- **Reproducibility** - Consistent database state across environments
- **Team Collaboration** - Multiple developers can work on schema changes

### **Why Kotlin?**
- **Null Safety** - Reduces runtime errors
- **Concise Syntax** - Less boilerplate code
- **Interoperability** - Works seamlessly with Java libraries
- **Modern Features** - Coroutines, data classes, extension functions

## 📄 **License**

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 **Support**

For issues and questions:
1. Check the troubleshooting section
2. Review the additional documentation files
3. Create an issue in the repository

---

**🚀 Your RushHour Backend is now properly configured with environment validation, Docker support, and comprehensive documentation!** 