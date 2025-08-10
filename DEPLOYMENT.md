# TimeSheet Management System - Deployment Guide

This guide provides multiple deployment options for the TimeSheet Management System with MySQL database.

## 📋 Prerequisites

### For Kubernetes Deployment
- Kubernetes cluster (AKS, EKS, GKE, Minikube, Docker Desktop)
- kubectl (Kubernetes CLI)
- Docker (for building images)

### For Docker Compose Deployment
- Docker (version 20.10+)
- Docker Compose (version 2.0+)

### For Local Deployment
- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+

### For Cloud Deployment
- Cloud platform account (Azure, GCP, Heroku, etc.)
- Domain name (optional)

## 🚀 Quick Start

### 1. Kubernetes Deployment (Recommended)

```bash
# Clone the repository
git clone <your-repo-url>
cd TimeSheetManagement

# Deploy to Kubernetes
kubectl apply -k k8s/

# Check status
kubectl get all -n timesheet-management
```

### 2. Docker Compose Deployment

```bash
# Deploy with Docker Compose
./deploy.sh
# Select option 1: Docker Compose (Development)
```

Or manually:
```bash
# Start the application and database
docker-compose up --build -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 3. Production Deployment

```bash
# Copy environment variables
cp env.example .env
# Edit .env with your production values

# Deploy with production settings
docker-compose -f docker-compose.prod.yml up --build -d
```

## 🏠 Local Deployment

### 1. Kubernetes (Minikube/Docker Desktop)

```bash
# Start Minikube
minikube start

# Deploy application
kubectl apply -k k8s/

# Access application
minikube service timesheet-app-service -n timesheet-management
```

### 2. Docker Compose

```bash
# Start MySQL with Docker
docker run --name mysql \
  -e MYSQL_ROOT_PASSWORD=Globe@1234 \
  -e MYSQL_DATABASE=timesheetdb \
  -p 3306:3306 \
  -d mysql:8.0

# Or use your local MySQL installation
mysql -u root -p
CREATE DATABASE timesheetdb;
```

### 3. Standalone Application

```bash
# Build the application
mvn clean package -DskipTests

# Run the application
java -jar target/TimeSheetManagement-1.0-SNAPSHOT.jar
```

## ☁️ Cloud Deployment

### Google Cloud Deployment

1. **Create Compute Engine Instance**
```bash
gcloud compute instances create timesheet-app \
  --zone=us-central1-a \
  --machine-type=e2-medium \
  --image-family=debian-11 \
  --image-project=debian-cloud
```

2. **Create Cloud SQL MySQL Instance**
```bash
gcloud sql instances create timesheet-db \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=us-central1
```

3. **Deploy Application**
```bash
# SSH to instance
gcloud compute ssh timesheet-app --zone=us-central1-a

# Install Java and Maven
sudo apt-get update
sudo apt-get install openjdk-21-jdk maven -y

# Clone and deploy
git clone <your-repo-url>
cd TimeSheetManagement
./deploy.sh
```

### Azure Deployment

1. **Install Azure CLI**
```bash
# macOS
brew install azure-cli

# Windows
# Download from https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-windows
```

2. **Deploy to Azure**
```bash
# Login to Azure
az login

# Create resource group
az group create --name timesheet-rg --location eastus

# Create AKS cluster
az aks create --resource-group timesheet-rg --name timesheet-aks --node-count 2 --node-vm-size Standard_B2s

# Get cluster credentials
az aks get-credentials --resource-group timesheet-rg --name timesheet-aks

# Deploy application
kubectl apply -k k8s/
```

### Heroku Deployment

1. **Install Heroku CLI**
```bash
# macOS
brew install heroku/brew/heroku

# Windows
# Download from https://devcenter.heroku.com/articles/heroku-cli
```

2. **Deploy to Heroku**
```bash
# Login to Heroku
heroku login

# Create app
heroku create your-timesheet-app

# Add MySQL addon
heroku addons:create jawsdb:kitefin

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JWT_SECRET=your-secret-key

# Deploy
git push heroku main
```

### Railway Deployment

1. **Connect Repository**
   - Go to [Railway](https://railway.app)
   - Connect your GitHub repository
   - Add MySQL service

2. **Configure Environment Variables**
   - `SPRING_PROFILES_ACTIVE=prod`
   - `JWT_SECRET=your-secret-key`
   - Database URL will be auto-configured

3. **Deploy**
   - Railway will automatically deploy on git push

## 🔧 Configuration

### Environment Variables

Create a `.env` file for production:

```bash
# Database Configuration
MYSQL_ROOT_PASSWORD=YourSecureRootPassword123!
MYSQL_USER=timesheet_user
MYSQL_PASSWORD=YourSecureUserPassword123!

# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-here-make-it-long-and-secure-for-production-use-at-least-512-bits-for-hs512-algorithm

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

### Application Properties

The application uses different profiles:

- **Development**: `application.properties`
- **Production**: `application-prod.properties`

Key differences:
- Production disables SQL logging
- Production uses connection pooling
- Production has security headers
- Production validates database schema

## 📊 Database Setup

### Database Initialization

The deployment process includes automatic database initialization:

1. **Schema Creation**: JPA/Hibernate creates tables automatically
2. **Test Data Loading**: `create_test_data.sql` populates initial data
3. **Verification**: Database initialization script verifies setup

### Initial Data

The application includes test data in `create_test_data.sql`:

```sql
-- Default users
admin/password123
manager/password123
employee1/password123
employee2/password123
```

### Database Schema

The application uses JPA/Hibernate with auto-schema generation:
- **Development**: `spring.jpa.hibernate.ddl-auto=update`
- **Production**: `spring.jpa.hibernate.ddl-auto=update` (for initial setup)

### Manual Database Initialization

If you need to initialize the database manually:

```bash
# For local database
./init-database.sh

# For remote database (e.g., RDS)
./init-database.sh -h your-db-host -u your-user -w your-password

# Skip test data
./init-database.sh --no-data
```

## 🔒 Security Configuration

### JWT Configuration
- Secret key should be at least 512 bits
- Token expiration: 24 hours (configurable)
- Algorithm: HS512

### Database Security
- Use strong passwords
- Enable SSL connections in production
- Restrict database access to application servers

### Application Security
- CORS configured for frontend domains
- Spring Security with role-based access
- Input validation and sanitization

## 📈 Monitoring and Logging

### Application Logs
```bash
# Docker logs
docker-compose logs -f timesheet-app

# Local logs
tail -f logs/application.log
```

### Database Monitoring
```bash
# Check database status
docker-compose exec mysql mysqladmin ping

# Monitor queries
docker-compose exec mysql mysql -u root -p -e "SHOW PROCESSLIST;"
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/api/health

# Database health
mysqladmin ping -h localhost -u root -p
```

## 🚨 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check if MySQL is running
   docker-compose ps mysql
   
   # Check logs
   docker-compose logs mysql
   ```

2. **Application Won't Start**
   ```bash
   # Check Java version
   java -version
   
   # Check Maven
   mvn -version
   
   # Check logs
   docker-compose logs timesheet-app
   ```

3. **Port Already in Use**
   ```bash
   # Find process using port 8080
   lsof -i :8080
   
   # Kill process
   kill -9 <PID>
   ```

### Performance Issues

1. **Database Performance**
   - Increase connection pool size
   - Add database indexes
   - Monitor slow queries

2. **Application Performance**
   - Enable JVM tuning
   - Use production JVM flags
   - Monitor memory usage

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Deploy to Heroku
      uses: akhileshns/heroku-deploy@v3.12.12
      with:
        heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
        heroku_app_name: ${{ secrets.HEROKU_APP_NAME }}
        heroku_email: ${{ secrets.HEROKU_EMAIL }}
```

## 📞 Support

For deployment issues:
1. Check the logs: `docker-compose logs -f`
2. Verify environment variables
3. Test database connectivity
4. Check firewall and network settings

## 🔄 Updates and Maintenance

### Application Updates
```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose up --build -d
```

### Database Backups
```bash
# Create backup
docker-compose exec mysql mysqldump -u root -p timesheetdb > backup.sql

# Restore backup
docker-compose exec -T mysql mysql -u root -p timesheetdb < backup.sql
```

### Security Updates
- Regularly update dependencies
- Rotate JWT secrets
- Update database passwords
- Monitor security advisories
