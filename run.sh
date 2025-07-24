#!/bin/bash

# RushHour Application Runner
# This script helps you run the application with different profiles

echo "🚗 RushHour Application Runner"
echo "=============================="

# Check if profile argument is provided
if [ "$1" = "dev" ]; then
    echo "🔧 Starting in DEVELOPMENT mode (MySQL Database)"
    ./gradlew bootRun --args='--spring.profiles.active=dev'

elif [ "$1" = "prod" ]; then
    echo "🚀 Starting in PRODUCTION mode (MySQL Database)"
    ./gradlew bootRun --args='--spring.profiles.active=prod'
elif [ "$1" = "test" ]; then
    echo "🧪 Starting in TEST mode (MySQL Database)"
    ./gradlew bootRun --args='--spring.profiles.active=test'
elif [ "$1" = "mysql" ]; then
    echo "🗄️  Starting with MySQL (Default Profile)"
    ./gradlew bootRun
else
    echo "Usage: $0 [dev|prod|test|mysql]"
    echo ""
    echo "Profiles:"
    echo "  dev   - Development mode with local MySQL database"
    echo "  prod  - Production mode with MySQL database"
    echo "  test  - Test mode with MySQL database"
    echo "  mysql - Default mode with MySQL database"
    echo ""
    echo "Examples:"
    echo "  $0 dev   # Start with local MySQL for development"
    echo "  $0 prod  # Start with MySQL for production"
    echo "  $0 test  # Start with MySQL for testing"
    echo "  $0 mysql # Start with MySQL (default)"
    exit 1
fi 