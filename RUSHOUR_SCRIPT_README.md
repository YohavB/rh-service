# RushHour Unified Script

This document describes the unified `rushhour.sh` script that combines all the functionality from the previous separate scripts (`run.sh`, `run-app.sh`, and `mysql-setup.sh`) into a single, comprehensive management tool.

## Overview

The `rushhour.sh` script provides a unified interface for managing the RushHour backend application, including:

- **Application Management**: Run the application with different Spring profiles
- **Database Management**: Complete MySQL Docker container lifecycle management
- **Development Workflow**: Quick start functionality for development
- **Status Monitoring**: System status checking and health monitoring

## Features

### 🎨 Enhanced User Experience
- **Colored Output**: Different colors for status, info, warnings, and errors
- **Emoji Indicators**: Visual indicators for different types of operations
- **Comprehensive Help**: Detailed help system with examples
- **Error Handling**: Robust error checking and user-friendly error messages

### 🗄️ Database Management
- **Container Lifecycle**: Start, stop, restart, remove MySQL containers
- **Data Management**: Reset database with complete data cleanup
- **Health Monitoring**: Wait for MySQL to be ready before proceeding
- **Direct Access**: Connect to database for manual operations
- **Migration Support**: Run Flyway migrations automatically

### 🚀 Application Management
- **Profile Support**: Run with dev, prod, test, or mysql profiles
- **Health Checks**: Monitor application health status
- **Quick Start**: One-command setup for development environment

## Usage

### Basic Commands

```bash
# Show help
./rushhour.sh help

# Show system status
./rushhour.sh status

# Quick start for development
./rushhour.sh quick-start
```

### Application Management

```bash
# Run with different profiles
./rushhour.sh run dev      # Development mode
./rushhour.sh run prod     # Production mode
./rushhour.sh run test     # Test mode
./rushhour.sh run mysql    # MySQL profile
```

### Database Management

```bash
# Database container management
./rushhour.sh db start     # Start MySQL container
./rushhour.sh db stop      # Stop MySQL container
./rushhour.sh db restart   # Restart MySQL container
./rushhour.sh db remove    # Remove MySQL container
./rushhour.sh db reset     # Reset database (remove container and data)

# Database operations
./rushhour.sh db status    # Show container status
./rushhour.sh db logs      # Show container logs
./rushhour.sh db connect   # Connect to MySQL database
./rushhour.sh db migrate   # Run database migrations
```

## Development Workflow

### Quick Start for Development

The easiest way to get started is using the quick-start command:

```bash
./rushhour.sh quick-start
```

This command will:
1. Start the MySQL container
2. Wait for MySQL to be ready
3. Run database migrations
4. Start the application in development mode

### Manual Development Setup

If you prefer to manage each step manually:

```bash
# 1. Start the database
./rushhour.sh db start

# 2. Run migrations
./rushhour.sh db migrate

# 3. Start the application
./rushhour.sh run dev
```

## Configuration

The script uses the following default configuration:

### Application Configuration
- **Port**: 8008
- **Health URL**: http://localhost:8008/api/v1/health
- **Application URL**: http://localhost:8008

### Database Configuration
- **Container Name**: rh-mysql-local
- **Database**: rh
- **Username**: admin
- **Password**: root
- **Port**: 3306

## Error Handling

The script includes comprehensive error handling:

- **Docker Check**: Verifies Docker is running before database operations
- **Container State**: Checks container existence and running state
- **MySQL Readiness**: Waits for MySQL to be ready before proceeding
- **Application Health**: Monitors application health status

## Migration from Old Scripts

### Old Commands → New Commands

| Old Command | New Command | Description |
|-------------|-------------|-------------|
| `./run.sh dev` | `./rushhour.sh run dev` | Run in development mode |
| `./run.sh prod` | `./rushhour.sh run prod` | Run in production mode |
| `./run.sh test` | `./rushhour.sh run test` | Run in test mode |
| `./run.sh mysql` | `./rushhour.sh run mysql` | Run with MySQL profile |
| `./run-app.sh` | `./rushhour.sh run dev` | Run in development mode |
| `./mysql-setup.sh start` | `./rushhour.sh db start` | Start MySQL container |
| `./mysql-setup.sh stop` | `./rushhour.sh db stop` | Stop MySQL container |
| `./mysql-setup.sh restart` | `./rushhour.sh db restart` | Restart MySQL container |
| `./mysql-setup.sh remove` | `./rushhour.sh db remove` | Remove MySQL container |
| `./mysql-setup.sh reset` | `./rushhour.sh db reset` | Reset database |
| `./mysql-setup.sh logs` | `./rushhour.sh db logs` | Show container logs |
| `./mysql-setup.sh status` | `./rushhour.sh db status` | Show container status |
| `./mysql-setup.sh connect` | `./rushhour.sh db connect` | Connect to database |
| `./mysql-setup.sh migrate` | `./rushhour.sh db migrate` | Run migrations |

## Benefits of the Unified Script

### 1. **Simplified Management**
- Single script for all operations
- Consistent interface across all functions
- Reduced cognitive load for developers

### 2. **Enhanced Functionality**
- Better error handling and user feedback
- Integrated health checks and status monitoring
- Quick start functionality for development

### 3. **Improved Developer Experience**
- Colored output with emoji indicators
- Comprehensive help system
- Robust error messages and guidance

### 4. **Better Integration**
- Seamless workflow between database and application management
- Automatic dependency checking
- Integrated migration support

## Troubleshooting

### Common Issues

1. **Docker not running**
   ```
   ❌ Docker is not running. Please start Docker and try again.
   ```
   **Solution**: Start Docker Desktop and try again.

2. **MySQL container not found**
   ```
   ❌ MySQL container does not exist. Run 'start' first.
   ```
   **Solution**: Run `./rushhour.sh db start` to create the container.

3. **Application not responding**
   ```
   ℹ️  Application: Not running
   ```
   **Solution**: Check if the application is running with `./rushhour.sh status`.

### Getting Help

```bash
# Show comprehensive help
./rushhour.sh help

# Show system status
./rushhour.sh status

# Check database status
./rushhour.sh db status
```

## Future Enhancements

The unified script is designed to be extensible. Potential future enhancements include:

- **Environment Configuration**: Support for different environment configurations
- **Backup/Restore**: Database backup and restore functionality
- **Log Management**: Enhanced log viewing and filtering
- **Performance Monitoring**: Application performance metrics
- **CI/CD Integration**: Support for continuous integration workflows

## Contributing

When adding new functionality to the script:

1. Follow the existing code structure and patterns
2. Add appropriate error handling
3. Include help documentation
4. Test thoroughly before committing
5. Update this README with new features 