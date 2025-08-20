# RushHour Backend (RH-BE)

A Spring Boot backend application for the RushHour service, built with Kotlin and designed for production deployment.

## Overview

RushHour Backend is a RESTful API service that manages users, cars, and their relationships. The application uses MySQL as its primary database and is designed to work with an external MySQL service (`rh-mysql`).

## Features

- ✅ **Spring Boot 2.7.1** with Kotlin
- ✅ **MySQL Database** with Flyway migrations
- ✅ **RESTful APIs** for user and car management
- ✅ **Push Notifications** via Expo Server SDK
- ✅ **Comprehensive Testing** (251 tests: 228 unit + 23 integration)
- ✅ **Production Ready** with proper error handling and logging
- ✅ **Environment Profiles** for different deployment scenarios
- ✅ **External Database Service** integration

## Quick Start

### Prerequisites

1. **Java 11** or higher
2. **Docker** (for local MySQL container)
3. **Gradle** (included via wrapper)

### Option 1: Local MySQL Container (Recommended for Development)

#### 1. Start Local MySQL Container
```bash
# Start MySQL container with the provided configuration
./mysql-setup.sh start
```

#### 2. Run Database Migrations
```bash
# Apply database schema
./mysql-setup.sh migrate
```

#### 3. Start RushHour Application
```bash
# Start in development mode
./run.sh dev
```

### Option 2: External MySQL Service

#### 1. Start MySQL Service
```bash
# Navigate to rh-mysql project
cd ../rh-mysql

# Start MySQL service
./scripts/start.sh
```

#### 2. Configure RushHour
```bash
# In RH-BE directory, create .env file
cp env.example .env

# Edit .env with your database credentials
nano .env
```

#### 3. Start RushHour
```bash
# Start RushHour application
./run.sh dev
```

## Project Structure

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
│   │   └── utils/          # Utility classes
│   ├── main/resources/
│   │   ├── db/migration/   # Flyway migration scripts
│   │   └── application*.properties  # Configuration profiles
│   └── test/               # Test suites
├── build.gradle.kts        # Build configuration
├── run.sh                  # Application runner
├── env.example             # Environment variables template
└── README.md              # This file
```

## Configuration

### Environment Variables (.env)
```bash

### Firebase Setup

This application requires Firebase credentials for authentication and notifications. Follow these steps to set up:

1. **Download Firebase Admin SDK credentials:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Select your project
   - Go to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Download the JSON file

2. **Place credentials in the correct location:**
   ```bash
   # Copy the downloaded file to the resources directory
   cp /path/to/downloaded/firebase-credentials.json src/main/resources/rushhour-firebase-adminsdk.json
   
   # Or place in root directory for development
   cp /path/to/downloaded/firebase-credentials.json rushhour-firebase-adminsdk.json
   ```

3. **Important:** The credentials file is automatically ignored by git for security reasons. Never commit this file to version control.

4. **For production deployment:** Ensure the credentials file is available in your deployment environment.
# Database Configuration (Local MySQL Container)
DATABASE_URL=jdbc:mysql://localhost:3306/rh?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=admin
DB_PASSWORD=root

# Application Configuration
SERVER_PORT=8008

# Logging Configuration
LOGGING_LEVEL_COM_YB_RH=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=WARN
LOGGING_LEVEL_ORG_HIBERNATE_SQL=WARN
```

### Local MySQL Container Configuration
The application is configured to work with a local MySQL Docker container with the following settings:
- **Container Name**: `rh-mysql-local`
- **Database**: `rh`
- **Username**: `admin`
- **Password**: `root`
- **Port**: `3306`
- **Volume**: `mysql_data` (persistent data storage)

These settings are configured in:
- `src/main/resources/application.properties`
- `src/main/resources/application-dev.properties`
- `build.gradle.kts` (Flyway configuration)

### Application Profiles

| Profile | Database | Purpose |
|---------|----------|---------|
| `dev` | MySQL (`rushhour_dev`) | Development with persistent data |
| `prod` | MySQL (`rushhour`) | Production environment |
| `test` | MySQL (`rushhour_test`) | Integration testing |
| `mysql` | MySQL (`rushhour`) | Default production-like |

## Database Schema

### Tables
1. **users** - User information with push notification tokens
2. **cars** - Vehicle information with plate numbers
3. **users_cars** - Many-to-many relationship between users and cars
4. **cars_relations** - Blocking relationships between cars

### Key Features
- **Foreign Key Constraints** - Ensures data integrity
- **Unique Constraints** - Prevents duplicate relationships
- **Indexes** - Optimizes query performance
- **Check Constraints** - Prevents self-blocking

## API Endpoints

### Users
- `POST /api/v1/user` - Create user
- `GET /api/v1/user?id={id}` - Get user by ID
- `GET /api/v1/user/by-email?email={email}` - Get user by email
- `PUT /api/v1/user` - Update user
- `PUT /api/v1/user/deactivate/{userId}` - Deactivate user
- `PUT /api/v1/user/activate/{userId}` - Activate user

### Cars
- `POST /api/v1/car` - Create car
- `GET /api/v1/car?id={id}` - Get car by ID
- `GET /api/v1/car/by-plate?plateNumber={plate}` - Get car by plate number
- `PUT /api/v1/car` - Update car
- `DELETE /api/v1/car/{id}` - Delete car

### User-Car Relationships
- `POST /api/v1/user-car` - Assign car to user
- `GET /api/v1/user-car?userId={id}` - Get user's cars
- `DELETE /api/v1/user-car` - Remove car from user

### Car Relations (Blocking)
- `POST /api/v1/car-relations` - Create blocking relationship
- `GET /api/v1/car-relations?carId={id}` - Get car's blocking relationships
- `DELETE /api/v1/car-relations` - Remove blocking relationship

### Notifications
- `POST /api/v1/notification` - Send push notification

## Available Commands

### Application Management
```bash
./run.sh dev    # Development mode
./run.sh prod   # Production mode
./run.sh test   # Test mode
./run.sh mysql  # Default mode
```

### MySQL Container Management
```bash
./mysql-setup.sh start     # Start MySQL container
./mysql-setup.sh stop      # Stop MySQL container
./mysql-setup.sh restart   # Restart MySQL container
./mysql-setup.sh status    # Check container status
./mysql-setup.sh logs      # View container logs
./mysql-setup.sh connect   # Connect to MySQL database
./mysql-setup.sh migrate   # Run database migrations
./mysql-setup.sh reset     # Reset database (remove container and data)
```

### Database Management
```bash
./gradlew flywayMigrate    # Run migrations
./gradlew flywayInfo       # Check migration status
./gradlew flywayClean      # Clean database (development only)
```

### Testing
```bash
./gradlew test             # Unit tests
./gradlew integrationTest  # Integration tests
./gradlew allTests         # All tests
./gradlew testCount        # Show test statistics
```

### Build
```bash
./gradlew build            # Build application
./gradlew bootRun          # Run application
./gradlew clean            # Clean build
```

## Development Workflow

### Local Development
1. **Start MySQL container:**
   ```bash
   ./mysql-setup.sh start
   ```

2. **Run database migrations:**
   ```bash
   ./mysql-setup.sh migrate
   ```

3. **Start application:**
   ```bash
   ./run.sh dev
   ```

4. **Run tests:**
   ```bash
   ./gradlew allTests
   ```

### Alternative: External MySQL Service
1. **Start MySQL service:**
   ```bash
   cd ../rh-mysql
   ./scripts/start.sh
   ```

2. **Configure environment:**
   ```bash
   cp env.example .env
   nano .env  # Set database credentials
   ```

3. **Start application:**
   ```bash
   ./run.sh dev
   ```

### Database Migrations
1. **Create new migration:**
   ```bash
   # Create file: src/main/resources/db/migration/V{version}__{description}.sql
   # Example: V2__Add_user_preferences.sql
   ```

2. **Run migrations:**
   ```bash
   ./gradlew flywayMigrate
   ```

3. **Check status:**
   ```bash
   ./gradlew flywayInfo
   ```

## Production Deployment

### 1. Deploy MySQL Service
```bash
# Copy rh-mysql to VPS
scp -r rh-mysql/ user@your-vps:/opt/

# Configure and start MySQL service
cd /opt/rh-mysql
cp env.example .env
nano .env  # Set production credentials
./scripts/start.sh
```

### 2. Deploy RushHour Application
```bash
# Copy RH-BE to VPS
scp -r RH-BE/ user@your-vps:/opt/

# Configure application
cd /opt/RH-BE
cp env.example .env
nano .env  # Set database connection

# Build and start application
./gradlew build
./run.sh prod
```

### 3. Configure Firewall
```bash
sudo ufw allow 8008/tcp  # Application port
sudo ufw allow 3306/tcp  # MySQL port (if external access needed)
```

## Testing

### Test Structure
- **Unit Tests** (228 tests) - Test individual components
- **Integration Tests** (23 tests) - Test with real database
- **Test Coverage** - JaCoCo reporting configured

### Running Tests
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

### Test Configuration
- Unit tests use H2 in-memory database
- Integration tests use MySQL via TestContainers
- Separate test profiles for different scenarios

## Error Handling

### Error Types
- `AUTHENTICATION` - Authentication failures
- `INVALID_INPUT` - Invalid request data
- `ENTITY_NOT_FOUND` - Resource not found
- `RESOURCE_ALREADY_EXISTS` - Duplicate resources
- `DB_ACCESS` - Database access issues
- `HTTP_CALL` - External service failures

### Error Response Format
```json
{
  "cause": "Error description",
  "errorCode": 400
}
```

## Monitoring and Logging

### Logging Configuration
- **Application logs** - INFO level for business operations
- **SQL logs** - WARN level in production
- **Web logs** - WARN level for HTTP requests
- **Structured logging** - JSON format for production

### Health Checks
- Application health endpoint (when Spring Boot Actuator is added)
- Database connection monitoring
- External service health checks

## Security Considerations

### Current Implementation
- Input validation with Spring Validation
- SQL injection prevention via JPA
- Error message sanitization
- Environment-based configuration

### Recommended Improvements
- [ ] Add Spring Security for authentication
- [ ] Implement JWT token-based authorization
- [ ] Add rate limiting
- [ ] Enable HTTPS/TLS
- [ ] Add API documentation (OpenAPI/Swagger)
- [ ] Implement audit logging

## Performance Optimization

### Database
- Connection pooling with HikariCP
- Optimized indexes on frequently queried columns
- Query optimization with proper JPA annotations

### Application
- Async processing for notifications
- Efficient DTO mapping
- Proper exception handling to prevent memory leaks

## Troubleshooting

### Common Issues

**1. Database Connection Failed**
```bash
# Check if MySQL container is running
./mysql-setup.sh status

# Check container logs
./mysql-setup.sh logs

# Verify connection details
echo "Database URL: jdbc:mysql://localhost:3306/rh"
echo "Username: admin"
echo "Password: root"
```

**1a. External MySQL Service Connection Failed**
```bash
# Check if MySQL service is running
cd ../rh-mysql
./scripts/status.sh

# Verify connection details in .env
cat .env | grep DB_
```

**2. Migration Errors**
```bash
# Check migration status
./gradlew flywayInfo

# Repair if needed
./gradlew flywayRepair
```

**3. Build Failures**
```bash
# Clean and rebuild
./gradlew clean build

# Check Java version
java -version
```

**4. Test Failures**
```bash
# Run tests with verbose output
./gradlew test --info

# Check test database configuration
cat src/test/resources/application.properties
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## Architecture Decisions

### Why External MySQL Service?
- **Separation of Concerns** - Database and application are independent
- **Scalability** - Can scale database and application separately
- **Maintenance** - Database maintenance doesn't affect application
- **Reusability** - MySQL service can be used by other projects

### Why Flyway Migrations?
- **Version Control** - Database schema changes are tracked
- **Reproducibility** - Consistent database state across environments
- **Rollback Support** - Can rollback schema changes
- **Team Collaboration** - Multiple developers can work on schema changes

### Why Kotlin?
- **Null Safety** - Reduces runtime errors
- **Concise Syntax** - Less boilerplate code
- **Interoperability** - Works seamlessly with Java libraries
- **Modern Features** - Coroutines, data classes, extension functions

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
1. Check the troubleshooting section
2. Review the rh-mysql project documentation
3. Create an issue in the repository 