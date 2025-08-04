# Verification Scripts

This folder contains scripts for running different types of tests and code coverage analysis.

## Available Scripts

### 1. `run-unit-tests.sh`
Runs only the unit tests (excluding integration tests).

```bash
./verification/run-unit-tests.sh
```

**Output:** Test results in `build/reports/tests/test/index.html`

### 2. `run-integration-tests.sh`
Runs only the integration tests.

```bash
./verification/run-integration-tests.sh
```

**Output:** Test results in `build/reports/tests/integrationTest/index.html`

### 3. `run-all-tests.sh`
Runs both unit tests and integration tests.

```bash
./verification/run-all-tests.sh
```

**Output:** 
- Unit test results in `build/reports/tests/test/index.html`
- Integration test results in `build/reports/tests/integrationTest/index.html`

### 4. `run-jacoco-coverage.sh`
Runs all tests with JaCoCo code coverage analysis.

```bash
./verification/run-jacoco-coverage.sh
```

**Output:** 
- HTML coverage report in `build/jacocoHtml/index.html`
- XML coverage report in `build/reports/jacoco/test/jacocoTestReport.xml`
- CSV coverage report in `build/reports/jacoco/test/jacocoTestReport.csv`

## Usage

All scripts can be run from any directory:

```bash
# From any directory
./verification/run-unit-tests.sh
./verification/run-integration-tests.sh
./verification/run-all-tests.sh
./verification/run-jacoco-coverage.sh

# Or with full path from anywhere
/path/to/RH-BE/verification/run-unit-tests.sh
/path/to/RH-BE/verification/run-integration-tests.sh
/path/to/RH-BE/verification/run-all-tests.sh
/path/to/RH-BE/verification/run-jacoco-coverage.sh
```

## Prerequisites

- Java 17 or higher
- Gradle (included in the project)
- MySQL database running (for integration tests)

## Notes

- All scripts use `--no-daemon` flag to ensure clean execution
- All scripts perform a clean build before running tests
- Scripts will exit with error code 1 if any tests fail
- Integration tests require a running MySQL database with the correct schema 