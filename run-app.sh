#!/bin/bash

echo "🚀 Starting Rush Hour Backend Service..."
echo "📍 Application will be available at: http://localhost:8008"
echo "📊 Health check: http://localhost:8008/api/v1/health"
echo ""

# Set development profile
export SPRING_PROFILES_ACTIVE=dev

# Run the application
./gradlew bootRun 