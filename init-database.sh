#!/bin/bash

# Database Initialization Script for TimeSheet Management System
# This script initializes the database with schema and test data

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Default values
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD="Globe@1234"
DB_NAME="timesheetdb"
INIT_DATA=true

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --host HOST       Database host (default: localhost)"
    echo "  -p, --port PORT       Database port (default: 3306)"
    echo "  -u, --user USER       Database user (default: root)"
    echo "  -w, --password PASS   Database password (default: Globe@1234)"
    echo "  -d, --database NAME   Database name (default: timesheetdb)"
    echo "  --no-data             Skip test data initialization"
    echo "  --help                Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                                    # Initialize local database"
    echo "  $0 -h my-rds-endpoint -u admin -w mypass  # Initialize RDS database"
    echo "  $0 --no-data                          # Initialize schema only"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--host)
            DB_HOST="$2"
            shift 2
            ;;
        -p|--port)
            DB_PORT="$2"
            shift 2
            ;;
        -u|--user)
            DB_USER="$2"
            shift 2
            ;;
        -w|--password)
            DB_PASSWORD="$2"
            shift 2
            ;;
        -d|--database)
            DB_NAME="$2"
            shift 2
            ;;
        --no-data)
            INIT_DATA=false
            shift
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Function to check if MySQL client is available
check_mysql_client() {
    if ! command -v mysql &> /dev/null; then
        print_error "MySQL client is not installed. Please install it first."
        print_status "On Ubuntu/Debian: sudo apt-get install mysql-client"
        print_status "On CentOS/RHEL: sudo yum install mysql"
        print_status "On macOS: brew install mysql-client"
        exit 1
    fi
}

# Function to wait for database to be ready
wait_for_database() {
    print_status "Waiting for database to be ready..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; then
            print_success "Database is ready!"
            return 0
        fi
        
        print_status "Attempt $attempt/$max_attempts: Database not ready yet, waiting..."
        sleep 10
        ((attempt++))
    done
    
    print_error "Database is not ready after $max_attempts attempts"
    exit 1
}

# Function to create database
create_database() {
    print_status "Creating database '$DB_NAME' if it doesn't exist..."
    
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    
    print_success "Database '$DB_NAME' is ready"
}

# Function to initialize schema (using Spring Boot JPA)
initialize_schema() {
    print_status "Initializing database schema..."
    print_status "Note: Schema will be created by Spring Boot JPA on first startup"
    print_status "Make sure the application is configured with: spring.jpa.hibernate.ddl-auto=update"
}

# Function to initialize test data
initialize_test_data() {
    if [ "$INIT_DATA" = false ]; then
        print_warning "Skipping test data initialization (--no-data flag)"
        return 0
    fi
    
    print_status "Initializing test data..."
    
    # Check if test data file exists
    if [ ! -f "create_test_data.sql" ]; then
        print_error "Test data file 'create_test_data.sql' not found"
        print_status "Please make sure the file exists in the current directory"
        exit 1
    fi
    
    # Run test data script
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < create_test_data.sql
    
    print_success "Test data initialized successfully"
}

# Function to verify initialization
verify_initialization() {
    print_status "Verifying database initialization..."
    
    # Check if tables exist
    local table_count=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SHOW TABLES;" | wc -l)
    
    if [ "$table_count" -gt 1 ]; then
        print_success "Database contains $((table_count - 1)) tables"
    else
        print_warning "No tables found. Schema will be created when application starts"
    fi
    
    # Check if test data exists
    if [ "$INIT_DATA" = true ]; then
        local user_count=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" -e "SELECT COUNT(*) FROM User;" 2>/dev/null | tail -n 1)
        
        if [ "$user_count" -gt 0 ]; then
            print_success "Test data verified: $user_count users found"
        else
            print_warning "No test data found. Data will be created when application starts"
        fi
    fi
}

# Function to show connection info
show_connection_info() {
    print_status "Database connection information:"
    echo "  Host: $DB_HOST"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  User: $DB_USER"
    echo ""
    print_status "Test data initialization: $([ "$INIT_DATA" = true ] && echo "Enabled" || echo "Disabled")"
}

# Main execution
main() {
    print_status "Starting database initialization for TimeSheet Management System"
    echo ""
    
    show_connection_info
    echo ""
    
    # Check prerequisites
    check_mysql_client
    
    # Initialize database
    wait_for_database
    create_database
    initialize_schema
    initialize_test_data
    verify_initialization
    
    echo ""
    print_success "Database initialization completed successfully!"
    print_status "You can now start the TimeSheet Management application"
    
    if [ "$INIT_DATA" = true ]; then
        echo ""
        print_status "Default test users:"
        echo "  admin/password123"
        echo "  manager/password123"
        echo "  employee1/password123"
        echo "  employee2/password123"
    fi
}

# Run main function
main
