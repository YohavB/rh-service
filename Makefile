# Makefile for RushHour Backend Docker Management
# Provides easy commands for different environments

.PHONY: help build-default build-prod run-default run-prod stop clean logs test

# Default target
help:
	@echo "RushHour Backend Docker Management"
	@echo "=================================="
	@echo ""
	@echo "Available commands:"
	@echo "  build-local      Build Docker image for local environment"
	@echo "  build-prod       Build Docker image for production environment"
	@echo "  run-local        Run application with local environment"
	@echo "  run-prod         Run application with production environment"
	@echo "  stop             Stop all services"
	@echo "  clean            Stop and remove all containers, networks, and volumes"
	@echo "  logs             View logs for all services"
	@echo "  logs-backend     View logs for backend service only"
	@echo "  test             Run tests in container"
	@echo "  shell            Open shell in backend container"
	@echo "  db-shell         Open MySQL shell"
	@echo "  health           Check health of all services"
	@echo "  backup           Create database backup"
	@echo "  restore          Restore database from backup"
	@echo "  migrate-local    Run Flyway migrations for local environment"
	@echo "  migrate-prod     Run Flyway migrations for production environment"
	@echo "  migrate-info-local Show Flyway migration info for local environment"
	@echo "  migrate-info-prod Show Flyway migration info for production environment"
	@echo ""

# Build commands
build-local:
	@echo "Building Docker image for local environment..."
	./build-docker.sh local

build-prod:
	@echo "Building Docker image for production environment..."
	./build-docker.sh prod

# Run commands
run-local:
	@echo "Starting application with local environment..."
	@echo "Use: docker run -p 8008:8008 --env-file env.local rh-backend:local"

run-prod:
	@echo "Starting application with production environment..."
	@echo "Use: docker run -p 8008:8008 --env-file env.prod rh-backend:prod"

# Management commands
stop:
	@echo "Stopping all containers..."
	@echo "Use: docker stop \$(docker ps -q --filter ancestor=rh-backend)"

clean:
	@echo "Cleaning up all containers and images..."
	docker system prune -f

logs:
	@echo "Viewing logs for containers..."
	@echo "Use: docker logs \$(docker ps -q --filter ancestor=rh-backend)"

logs-backend:
	@echo "Viewing logs for backend container..."
	@echo "Use: docker logs \$(docker ps -q --filter ancestor=rh-backend)"

# Development commands
test:
	@echo "Running tests locally..."
	./gradlew test

test-integration:
	@echo "Running integration tests locally..."
	./gradlew integrationTest

shell:
	@echo "Opening shell in running container..."
	@echo "Use: docker exec -it \$(docker ps -q --filter ancestor=rh-backend) /bin/bash"

db-shell:
	@echo "Opening MySQL shell..."
	@echo "Note: Using Coolify database. Connect directly to: u08w4kcg4sows0s0ksokcg4s:3306"

# Health and monitoring
health:
	@echo "Checking health of containers..."
	@echo "Use: docker ps --filter ancestor=rh-backend"
	@echo ""
	@echo "Health check endpoints:"
	@echo "  Backend: http://localhost:8008/health"

# Database management
backup:
	@echo "Creating database backup..."
	@echo "Note: Using Coolify database. Backups should be managed through Coolify dashboard."
	@echo "Database connection: u08w4kcg4sows0s0ksokcg4s:3306/default"

restore:
	@echo "Database restore:"
	@echo "Note: Using Coolify database. Restores should be managed through Coolify dashboard."
	@echo "Database connection: u08w4kcg4sows0s0ksokcg4s:3306/default"

restore-file:
	@echo "Database restore:"
	@echo "Note: Using Coolify database. Restores should be managed through Coolify dashboard."
	@echo "Database connection: u08w4kcg4sows0s0ksokcg4s:3306/default"

# Flyway migration tasks
migrate-local:
	@echo "Running Flyway migrations for local environment..."
	./gradlew flywayMigrateLocal

migrate-prod:
	@echo "Running Flyway migrations for production environment..."
	./gradlew flywayMigrateProd

migrate-info-local:
	@echo "Showing Flyway migration info for local environment..."
	./gradlew flywayInfoLocal

migrate-info-prod:
	@echo "Showing Flyway migration info for production environment..."
	./gradlew flywayInfoProd

# Quick development setup
dev-setup: build-local run-local
	@echo "Local development environment setup complete!"
	@echo "Application is running at http://localhost:8008"
	@echo "Database: Using Coolify database (u08w4kcg4sows0s0ksokcg4s:3306/default)"
	@echo "Use 'make logs' to view logs"

prod-setup: build-prod run-prod
	@echo "Production environment setup complete!"
	@echo "Application is running at http://localhost:8008"
	@echo "Database: Using Coolify database (u08w4kcg4sows0s0ksokcg4s:3306/default)"
	@echo "Use 'make logs' to view logs"

# Utility commands
status:
	@echo "Service status:"
	@echo "Use: docker ps --filter ancestor=rh-backend"

restart:
	@echo "Restarting containers..."
	@echo "Use: docker restart \$(docker ps -q --filter ancestor=rh-backend)"

restart-backend:
	@echo "Restarting backend container..."
	@echo "Use: docker restart \$(docker ps -q --filter ancestor=rh-backend)"

# Environment info
env-info:
	@echo "Current environment configuration:"
	@echo "  BUILD_ENV: $(BUILD_ENV)"
	@echo "  SPRING_PROFILES_ACTIVE: $(shell printenv SPRING_PROFILES_ACTIVE 2>/dev/null || echo "Not set")"
	@echo ""
	@echo "Environment files:"
	@ls -la env.* 2>/dev/null || echo "No environment files found"
