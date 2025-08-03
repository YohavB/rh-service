#!/bin/bash

# RushHour Unified Application Manager
# This script provides comprehensive management for the RushHour application
# including database setup, application running, and various utilities

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Application configuration
APP_NAME="RushHour Backend Service"
APP_PORT=8008
HEALTH_URL="http://localhost:${APP_PORT}/api/v1/health"
APP_URL="http://localhost:${APP_PORT}"

# Database configuration
DB_CONTAINER_NAME="rh-mysql-local"
DB_NAME="rh"
DB_USER="admin"
DB_PASSWORD="root"
DB_PORT=3306

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "${CYAN}🚗 $1${NC}"
    echo "=============================="
}

# Function to check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to check if MySQL container exists
mysql_container_exists() {
    docker ps -a --filter name=${DB_CONTAINER_NAME} --format "{{.Names}}" | grep -q ${DB_CONTAINER_NAME}
}

# Function to check if MySQL container is running
mysql_container_running() {
    docker ps --filter name=${DB_CONTAINER_NAME} --format "{{.Names}}" | grep -q ${DB_CONTAINER_NAME}
}

# Function to wait for MySQL to be ready
wait_for_mysql() {
    print_info "Waiting for MySQL to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker exec ${DB_CONTAINER_NAME} mysqladmin ping -u${DB_USER} -p${DB_PASSWORD} --silent 2>/dev/null; then
            print_status "MySQL is ready!"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "MySQL failed to start within 60 seconds"
    return 1
}

# Database management functions
start_database() {
    print_header "Starting MySQL Database"
    check_docker
    
    if mysql_container_running; then
        print_warning "MySQL container is already running"
        return 0
    fi
    
    if mysql_container_exists; then
        print_info "Starting existing MySQL container..."
        docker start ${DB_CONTAINER_NAME}
    else
        print_info "Creating and starting new MySQL container..."
        docker run -d \
          --name ${DB_CONTAINER_NAME} \
          -e MYSQL_ROOT_PASSWORD=${DB_PASSWORD} \
          -e MYSQL_DATABASE=${DB_NAME} \
          -e MYSQL_USER=${DB_USER} \
          -e MYSQL_PASSWORD=${DB_PASSWORD} \
          -v mysql_data:/var/lib/mysql \
          -p ${DB_PORT}:3306 \
          mysql:8
    fi
    
    wait_for_mysql
    print_status "MySQL container started successfully!"
    print_info "Database: ${DB_NAME}"
    print_info "Username: ${DB_USER}"
    print_info "Password: ${DB_PASSWORD}"
    print_info "Port: ${DB_PORT}"
}

stop_database() {
    print_header "Stopping MySQL Database"
    check_docker
    
    if mysql_container_running; then
        docker stop ${DB_CONTAINER_NAME}
        print_status "MySQL container stopped!"
    else
        print_warning "MySQL container is not running"
    fi
}

restart_database() {
    print_header "Restarting MySQL Database"
    check_docker
    
    if mysql_container_exists; then
        docker restart ${DB_CONTAINER_NAME}
        wait_for_mysql
        print_status "MySQL container restarted!"
    else
        print_error "MySQL container does not exist. Run 'start' first."
        exit 1
    fi
}

remove_database() {
    print_header "Removing MySQL Database"
    check_docker
    
    docker stop ${DB_CONTAINER_NAME} 2>/dev/null || true
    docker rm ${DB_CONTAINER_NAME} 2>/dev/null || true
    print_status "MySQL container removed!"
}

reset_database() {
    print_header "Resetting MySQL Database"
    check_docker
    
    docker stop ${DB_CONTAINER_NAME} 2>/dev/null || true
    docker rm ${DB_CONTAINER_NAME} 2>/dev/null || true
    docker volume rm mysql_data 2>/dev/null || true
    print_status "Database reset complete!"
    print_info "Run '$0 db start' to create a fresh MySQL container"
}

show_database_status() {
    print_header "MySQL Database Status"
    check_docker
    
    docker ps -a --filter name=${DB_CONTAINER_NAME}
}

show_database_logs() {
    print_header "MySQL Database Logs"
    check_docker
    
    if mysql_container_exists; then
        docker logs ${DB_CONTAINER_NAME}
    else
        print_error "MySQL container does not exist"
        exit 1
    fi
}

connect_database() {
    print_header "Connecting to MySQL Database"
    check_docker
    
    if mysql_container_running; then
        docker exec -it ${DB_CONTAINER_NAME} mysql -u ${DB_USER} -p${DB_PASSWORD} ${DB_NAME}
    else
        print_error "MySQL container is not running. Run '$0 db start' first."
        exit 1
    fi
}

run_migrations() {
    print_header "Running Database Migrations"
    
    if ! mysql_container_running; then
        print_error "MySQL container is not running. Run '$0 db start' first."
        exit 1
    fi
    
    ./gradlew flywayMigrate
    print_status "Database migrations completed!"
}

# Application management functions
run_application() {
    local profile=${1:-dev}
    
    print_header "Starting ${APP_NAME}"
    print_info "Profile: ${profile}"
    print_info "Application URL: ${APP_URL}"
    print_info "Health Check: ${HEALTH_URL}"
    echo ""
    
    # Set the Spring profile
    export SPRING_PROFILES_ACTIVE=${profile}
    
    # Run the application
    ./gradlew bootRun
}

run_dev() {
    run_application "dev"
}

run_prod() {
    run_application "prod"
}

run_test() {
    run_application "test"
}

run_mysql() {
    run_application "mysql"
}

# Utility functions
show_help() {
    print_header "${APP_NAME} Manager"
    echo ""
    echo "Usage: $0 [command] [subcommand]"
    echo ""
    echo "Commands:"
    echo ""
    echo "  run [profile]     - Run the application with specified profile"
    echo "    Profiles: dev, prod, test, mysql"
    echo "    Examples:"
    echo "      $0 run dev    # Run in development mode"
    echo "      $0 run prod   # Run in production mode"
    echo "      $0 run test   # Run in test mode"
    echo "      $0 run mysql  # Run with MySQL profile"
    echo ""
    echo "  db [command]      - Database management commands"
    echo "    Commands:"
    echo "      start         - Start MySQL container"
    echo "      stop          - Stop MySQL container"
    echo "      restart       - Restart MySQL container"
    echo "      remove        - Remove MySQL container"
    echo "      reset         - Reset database (remove container and data)"
    echo "      status        - Show container status"
    echo "      logs          - Show container logs"
    echo "      connect       - Connect to MySQL database"
    echo "      migrate       - Run database migrations"
    echo ""
    echo "  quick-start      - Quick start (start DB + run in dev mode)"
    echo "  status           - Show overall status"
    echo "  help             - Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 quick-start           # Start everything for development"
    echo "  $0 db start              # Start MySQL database"
    echo "  $0 db migrate            # Run database migrations"
    echo "  $0 run dev               # Run application in dev mode"
    echo "  $0 db connect            # Connect to database"
    echo ""
    echo "Database Configuration:"
    echo "  Database: ${DB_NAME}"
    echo "  Username: ${DB_USER}"
    echo "  Password: ${DB_PASSWORD}"
    echo "  Port: ${DB_PORT}"
}

show_status() {
    print_header "System Status"
    echo ""
    
    # Check Docker
    if docker info >/dev/null 2>&1; then
        print_status "Docker: Running"
    else
        print_error "Docker: Not running"
    fi
    
    # Check MySQL container
    if mysql_container_exists; then
        if mysql_container_running; then
            print_status "MySQL Container: Running"
        else
            print_warning "MySQL Container: Stopped"
        fi
    else
        print_info "MySQL Container: Not created"
    fi
    
    # Check if application is running
    if curl -s ${HEALTH_URL} >/dev/null 2>&1; then
        print_status "Application: Running (${APP_URL})"
    else
        print_info "Application: Not running"
    fi
    
    echo ""
}

quick_start() {
    print_header "Quick Start - Setting up everything for development"
    echo ""
    
    # Start database
    start_database
    
    # Run migrations
    run_migrations
    
    # Start application
    echo ""
    print_info "Starting application in development mode..."
    run_dev
}

# Main script logic
case "$1" in
    "run")
        case "$2" in
            "dev"|"development")
                run_dev
                ;;
            "prod"|"production")
                run_prod
                ;;
            "test")
                run_test
                ;;
            "mysql")
                run_mysql
                ;;
            *)
                print_error "Invalid profile. Use: dev, prod, test, mysql"
                echo "Usage: $0 run [dev|prod|test|mysql]"
                exit 1
                ;;
        esac
        ;;
    "db"|"database")
        case "$2" in
            "start")
                start_database
                ;;
            "stop")
                stop_database
                ;;
            "restart")
                restart_database
                ;;
            "remove")
                remove_database
                ;;
            "reset")
                reset_database
                ;;
            "status")
                show_database_status
                ;;
            "logs")
                show_database_logs
                ;;
            "connect")
                connect_database
                ;;
            "migrate")
                run_migrations
                ;;
            *)
                print_error "Invalid database command"
                echo "Usage: $0 db [start|stop|restart|remove|reset|status|logs|connect|migrate]"
                exit 1
                ;;
        esac
        ;;
    "quick-start"|"quickstart")
        quick_start
        ;;
    "status")
        show_status
        ;;
    "help"|"--help"|"-h"|"")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        echo ""
        echo "Run '$0 help' for usage information"
        exit 1
        ;;
esac 