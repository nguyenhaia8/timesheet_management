#!/bin/bash

# TimeSheet Management System Deployment Script
# This script provides multiple deployment options

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

# Function to check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
}

# Function to check if Java is installed
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 21 first."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt "21" ]; then
        print_error "Java 21 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
}

# Function to check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        exit 1
    fi
}

# Function to deploy with Docker Compose (Development)
deploy_docker_dev() {
    print_status "Deploying with Docker Compose (Development)..."
    
    check_docker
    
    # Stop existing containers
    print_status "Stopping existing containers..."
    docker-compose down
    
    # Build and start containers
    print_status "Building and starting containers..."
    docker-compose up --build -d
    
    # Wait for services to be ready
    print_status "Waiting for services to be ready..."
    sleep 30
    
    # Initialize database with test data
    print_status "Initializing database with test data..."
    if [ -f "init-database.sh" ]; then
        chmod +x init-database.sh
        ./init-database.sh
    else
        print_warning "Database initialization script not found. Test data will be loaded automatically by Docker."
    fi
    
    # Check if services are running
    if docker-compose ps | grep -q "Up"; then
        print_success "Deployment completed successfully!"
        print_status "Application is running at: http://localhost:8080"
        print_status "Database is running at: localhost:3306"
        print_status "Default users: admin/password123, manager/password123, employee1/password123"
        print_status "Use 'docker-compose logs -f' to view logs"
    else
        print_error "Deployment failed. Check logs with 'docker-compose logs'"
        exit 1
    fi
}

# Function to deploy with Docker Compose (Production)
deploy_docker_prod() {
    print_status "Deploying with Docker Compose (Production)..."
    
    check_docker
    
    # Check if .env file exists
    if [ ! -f .env ]; then
        print_error ".env file not found. Please copy env.example to .env and configure it."
        exit 1
    fi
    
    # Stop existing containers
    print_status "Stopping existing containers..."
    docker-compose -f docker-compose.prod.yml down
    
    # Build and start containers
    print_status "Building and starting containers..."
    docker-compose -f docker-compose.prod.yml up --build -d
    
    # Wait for services to be ready
    print_status "Waiting for services to be ready..."
    sleep 45
    
    # Check if services are running
    if docker-compose -f docker-compose.prod.yml ps | grep -q "Up"; then
        print_success "Production deployment completed successfully!"
        print_status "Application is running at: http://localhost:8080"
        print_status "Database is running at: localhost:3306"
        print_status "Use 'docker-compose -f docker-compose.prod.yml logs -f' to view logs"
    else
        print_error "Production deployment failed. Check logs with 'docker-compose -f docker-compose.prod.yml logs'"
        exit 1
    fi
}

# Function to deploy locally with Maven
deploy_local() {
    print_status "Deploying locally with Maven..."
    
    check_java
    check_maven
    
    # Check if MySQL is running
    if ! mysqladmin ping -h localhost -u root -pGlobe@1234 &> /dev/null; then
        print_error "MySQL is not running. Please start MySQL first."
        print_status "You can use: docker run --name mysql -e MYSQL_ROOT_PASSWORD=Globe@1234 -e MYSQL_DATABASE=timesheetdb -p 3306:3306 -d mysql:8.0"
        exit 1
    fi
    
    # Build the application
    print_status "Building application..."
    mvn clean package -DskipTests
    
    # Run the application
    print_status "Starting application..."
    java -jar target/TimeSheetManagement-1.0-SNAPSHOT.jar &
    
    # Wait for application to start
    print_status "Waiting for application to start..."
    sleep 15
    
    # Check if application is running
    if curl -f http://localhost:8080/api/health &> /dev/null; then
        print_success "Local deployment completed successfully!"
        print_status "Application is running at: http://localhost:8080"
    else
        print_error "Local deployment failed. Check logs."
        exit 1
    fi
}

# Function to deploy to cloud (AWS, GCP, Azure)
deploy_cloud() {
    print_status "Cloud deployment options:"
    echo "1. AWS (EC2 + RDS)"
    echo "2. Google Cloud (Compute Engine + Cloud SQL)"
    echo "3. Azure (VM + Azure Database for MySQL)"
    echo "4. Heroku"
    echo "5. Railway"
    echo "6. DigitalOcean"
    
    read -p "Select cloud platform (1-6): " choice
    
    case $choice in
        1) deploy_aws ;;
        2) deploy_gcp ;;
        3) deploy_azure ;;
        4) deploy_heroku ;;
        5) deploy_railway ;;
        6) deploy_digitalocean ;;
        *) print_error "Invalid choice" ;;
    esac
}

# Function to deploy to AWS
deploy_aws() {
    print_status "AWS deployment guide:"
    echo "1. Create an EC2 instance (t3.medium or larger)"
    echo "2. Create an RDS MySQL instance"
    echo "3. Configure security groups"
    echo "4. Upload and run the application"
    echo ""
    echo "Commands:"
    echo "aws ec2 run-instances --image-id ami-12345678 --instance-type t3.medium --key-name your-key"
    echo "aws rds create-db-instance --db-instance-identifier timesheet-db --db-instance-class db.t3.micro --engine mysql"
}

# Function to deploy to Heroku
deploy_heroku() {
    print_status "Heroku deployment guide:"
    echo "1. Install Heroku CLI"
    echo "2. Create Heroku app: heroku create your-app-name"
    echo "3. Add MySQL addon: heroku addons:create jawsdb:kitefin"
    echo "4. Deploy: git push heroku main"
    echo "5. Set environment variables:"
    echo "   heroku config:set SPRING_PROFILES_ACTIVE=prod"
    echo "   heroku config:set JWT_SECRET=your-secret-key"
}

# Function to show deployment status
show_status() {
    print_status "Checking deployment status..."
    
    # Check if Docker containers are running
    if command -v docker &> /dev/null; then
        if docker-compose ps &> /dev/null; then
            print_status "Docker containers status:"
            docker-compose ps
        fi
    fi
    
    # Check if application is responding
    if curl -f http://localhost:8080/api/health &> /dev/null; then
        print_success "Application is running at http://localhost:8080"
    else
        print_warning "Application is not responding at http://localhost:8080"
    fi
    
    # Check if database is accessible
    if mysqladmin ping -h localhost -u root -pGlobe@1234 &> /dev/null; then
        print_success "Database is accessible"
    else
        print_warning "Database is not accessible"
    fi
}

# Function to stop deployment
stop_deployment() {
    print_status "Stopping deployment..."
    
    # Stop Docker containers
    if [ -f docker-compose.yml ]; then
        docker-compose down
    fi
    
    if [ -f docker-compose.prod.yml ]; then
        docker-compose -f docker-compose.prod.yml down
    fi
    
    # Stop local application
    pkill -f "TimeSheetManagement-1.0-SNAPSHOT.jar" || true
    
    print_success "Deployment stopped"
}

# Function to show logs
show_logs() {
    print_status "Showing logs..."
    
    if [ -f docker-compose.yml ]; then
        docker-compose logs -f
    elif [ -f docker-compose.prod.yml ]; then
        docker-compose -f docker-compose.prod.yml logs -f
    else
        print_warning "No Docker Compose files found"
    fi
}

# Main script
main() {
    echo "=========================================="
    echo "TimeSheet Management System Deployment"
    echo "=========================================="
    echo ""
    echo "Deployment options:"
    echo "1. Docker Compose (Development)"
    echo "2. Docker Compose (Production)"
    echo "3. Local deployment with Maven"
    echo "4. Cloud deployment"
    echo "5. Show deployment status"
    echo "6. Stop deployment"
    echo "7. Show logs"
    echo "8. Exit"
    echo ""
    
    read -p "Select option (1-8): " choice
    
    case $choice in
        1) deploy_docker_dev ;;
        2) deploy_docker_prod ;;
        3) deploy_local ;;
        4) deploy_cloud ;;
        5) show_status ;;
        6) stop_deployment ;;
        7) show_logs ;;
        8) print_status "Goodbye!"; exit 0 ;;
        *) print_error "Invalid option"; main ;;
    esac
}

# Run main function
main
