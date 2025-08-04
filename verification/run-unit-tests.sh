#!/bin/bash

# Script to run all unit tests
# This script runs only the unit tests (excluding integration tests)

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Go to the project root (one level up from verification folder)
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🧪 Running Unit Tests..."
echo "=========================="

# Change to project root and run tests
cd "$PROJECT_ROOT"
./gradlew clean test --no-daemon

echo ""
echo "✅ Unit tests completed successfully!"
echo "📊 Test results available in: build/reports/tests/test/index.html" 