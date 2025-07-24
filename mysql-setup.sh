#!/bin/bash

# MySQL Docker Container Setup for RushHour
# This script helps you manage your local MySQL container

echo "🗄️  RushHour MySQL Container Manager"
echo "===================================="

case "$1" in
    "start")
        echo "🚀 Starting MySQL container..."
        docker run -d \
          --name rh-mysql-local \
          -e MYSQL_ROOT_PASSWORD=root \
          -e MYSQL_DATABASE=rh \
          -e MYSQL_USER=admin \
          -e MYSQL_PASSWORD=root \
          -v mysql_data:/var/lib/mysql \
          -p 3306:3306 \
          mysql:8
        echo "✅ MySQL container started successfully!"
        echo "📊 Database: rh"
        echo "👤 Username: admin"
        echo "🔑 Password: root"
        echo "🌐 Port: 3306"
        ;;
    "stop")
        echo "🛑 Stopping MySQL container..."
        docker stop rh-mysql-local
        echo "✅ MySQL container stopped!"
        ;;
    "restart")
        echo "🔄 Restarting MySQL container..."
        docker restart rh-mysql-local
        echo "✅ MySQL container restarted!"
        ;;
    "remove")
        echo "🗑️  Removing MySQL container..."
        docker stop rh-mysql-local 2>/dev/null
        docker rm rh-mysql-local 2>/dev/null
        echo "✅ MySQL container removed!"
        ;;
    "logs")
        echo "📋 Showing MySQL container logs..."
        docker logs rh-mysql-local
        ;;
    "status")
        echo "📊 MySQL container status:"
        docker ps -a --filter name=rh-mysql-local
        ;;
    "connect")
        echo "🔌 Connecting to MySQL database..."
        docker exec -it rh-mysql-local mysql -u admin -proot rh
        ;;
    "migrate")
        echo "🔄 Running database migrations..."
        ./gradlew flywayMigrate
        ;;
    "reset")
        echo "🔄 Resetting database (removing container and data volume)..."
        docker stop rh-mysql-local 2>/dev/null
        docker rm rh-mysql-local 2>/dev/null
        docker volume rm mysql_data 2>/dev/null
        echo "✅ Database reset complete!"
        echo "💡 Run '$0 start' to create a fresh MySQL container"
        ;;
    *)
        echo "Usage: $0 [start|stop|restart|remove|logs|status|connect|migrate|reset]"
        echo ""
        echo "Commands:"
        echo "  start    - Start MySQL container"
        echo "  stop     - Stop MySQL container"
        echo "  restart  - Restart MySQL container"
        echo "  remove   - Remove MySQL container"
        echo "  logs     - Show container logs"
        echo "  status   - Show container status"
        echo "  connect  - Connect to MySQL database"
        echo "  migrate  - Run database migrations"
        echo "  reset    - Reset database (remove container and data)"
        echo ""
        echo "Database Configuration:"
        echo "  Database: rh"
        echo "  Username: admin"
        echo "  Password: root"
        echo "  Port: 3306"
        echo ""
        echo "Examples:"
        echo "  $0 start   # Start MySQL container"
        echo "  $0 migrate # Run migrations"
        echo "  $0 connect # Connect to database"
        echo "  $0 reset   # Reset everything"
        exit 1
        ;;
esac 