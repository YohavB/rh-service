# Database Setup and Migration Guide

## Overview
This project uses **Flyway** for database schema versioning and **Hibernate** for ORM. The database schema is managed through SQL migration scripts.

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
- **Check Constraints** - Prevents self-blocking (car cannot block itself)

## Profiles

### Production Profile (default)
- Uses Flyway for schema management
- Hibernate validates schema against entities
- File-based H2 database

### Development Profile
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
- Hibernate creates/updates schema automatically
- Flyway disabled
- SQL logging enabled

### Test Profile
```bash
./gradlew test --args='--spring.profiles.active=test'
```
- In-memory H2 database
- Hibernate manages schema
- No SQL logging

## Migration Process

### Creating a New Migration
1. Create a new SQL file in `src/main/resources/db/migration/`
2. Follow naming convention: `V{version}__{description}.sql`
3. Example: `V2__Add_user_preferences.sql`

### Running Migrations
- Migrations run automatically on application startup
- Use `spring.flyway.baseline-on-migrate=true` for existing databases

### Migration Scripts
- **V1__Create_initial_schema.sql** - Initial schema with all tables and constraints

## Database Integrity

### Fixed Issues
1. **Foreign Key References** - Now correctly reference `id` instead of `plate_number`
2. **Column Names** - Consistent naming convention
3. **Constraints** - Proper unique and check constraints
4. **Indexes** - Performance optimization for common queries

### Constraints
- Users: Unique email and push notification token
- Cars: Unique plate number
- User-Car: Unique user-car combination
- Car Relations: Unique blocking relationship, no self-blocking

## Development Workflow

1. **Development**: Use `dev` profile for schema changes
2. **Testing**: Use `test` profile for automated tests
3. **Production**: Use default profile with Flyway migrations

## Troubleshooting

### Common Issues
1. **Migration Conflicts**: Ensure migration version numbers are sequential
2. **Schema Validation Errors**: Check entity annotations match database schema
3. **Foreign Key Errors**: Verify referenced columns exist and have correct types

### Useful Commands
```bash
# Check Flyway status
./gradlew flywayInfo

# Clean and migrate database
./gradlew flywayClean flywayMigrate

# Validate schema
./gradlew flywayValidate
``` 