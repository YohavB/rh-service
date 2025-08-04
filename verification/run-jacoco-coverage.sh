#!/bin/bash

# Script to run JaCoCo code coverage for all tests
# This script runs all tests with code coverage analysis

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Go to the project root (one level up from verification folder)
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "📊 Running JaCoCo Code Coverage..."
echo "==================================="

# Change to project root and run tests
cd "$PROJECT_ROOT"
./gradlew clean jacocoTestReport --no-daemon

echo ""
echo "✅ JaCoCo coverage completed successfully!"
echo "📊 Coverage reports available in:"
echo "   - HTML report: build/jacocoHtml/index.html"
echo "   - XML report: build/reports/jacoco/test/jacocoTestReport.xml"
echo "   - CSV report: build/reports/jacoco/test/jacocoTestReport.csv"
