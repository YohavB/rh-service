#!/bin/bash

# Script to run all tests (unit + integration)
# This script runs both unit tests and integration tests

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Go to the project root (one level up from verification folder)
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🚀 Running All Tests..."
echo "========================"

# Change to project root and run tests
cd "$PROJECT_ROOT"
./gradlew clean allTests --no-daemon

echo ""
echo "✅ All tests completed successfully!"
echo "📊 Test results available in:"
echo "   - Unit tests: build/reports/tests/test/index.html"
echo "   - Integration tests: build/reports/tests/integrationTest/index.html" 