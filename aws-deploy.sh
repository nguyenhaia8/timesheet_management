#!/bin/bash

# AWS Deployment Script for TimeSheet Management System
# This script automates the deployment to AWS EC2 + RDS

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

# Configuration variables
PROJECT_NAME="timesheet-management"
REGION="us-east-1"
VPC_CIDR="10.0.0.0/16"
SUBNET_CIDR="10.0.1.0/24"
INSTANCE_TYPE="t3.medium"
DB_INSTANCE_CLASS="db.t3.micro"
KEY_NAME="timesheet-key"

# Function to check AWS CLI
check_aws_cli() {
    if ! command -v aws &> /dev/null; then
        print_error "AWS CLI is not installed. Please install it first."
        print_status "Installation guide: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
        exit 1
    fi
    
    if ! aws sts get-caller-identity &> /dev/null; then
        print_error "AWS CLI is not configured. Please run 'aws configure' first."
        exit 1
    fi
}

# Function to create VPC and networking
create_networking() {
    print_status "Creating VPC and networking components..."
    
    # Create VPC
    VPC_ID=$(aws ec2 create-vpc --cidr-block $VPC_CIDR --query 'Vpc.VpcId' --output text)
    aws ec2 create-tags --resources $VPC_ID --tags Key=Name,Value=$PROJECT_NAME-vpc
    
    # Create Internet Gateway
    IGW_ID=$(aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text)
    aws ec2 create-tags --resources $IGW_ID --tags Key=Name,Value=$PROJECT_NAME-igw
    aws ec2 attach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IGW_ID
    
    # Create Subnet
    SUBNET_ID=$(aws ec2 create-subnet --vpc-id $VPC_ID --cidr-block $SUBNET_CIDR --query 'Subnet.SubnetId' --output text)
    aws ec2 create-tags --resources $SUBNET_ID --tags Key=Name,Value=$PROJECT_NAME-subnet
    
    # Create Route Table
    ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id $VPC_ID --query 'RouteTable.RouteTableId' --output text)
    aws ec2 create-tags --resources $ROUTE_TABLE_ID --tags Key=Name,Value=$PROJECT_NAME-rt
    aws ec2 create-route --route-table-id $ROUTE_TABLE_ID --destination-cidr-block 0.0.0.0/0 --gateway-id $IGW_ID
    aws ec2 associate-route-table --subnet-id $SUBNET_ID --route-table-id $ROUTE_TABLE_ID
    
    print_success "Networking components created successfully"
    echo "VPC_ID=$VPC_ID" > aws-config.txt
    echo "SUBNET_ID=$SUBNET_ID" >> aws-config.txt
    echo "IGW_ID=$IGW_ID" >> aws-config.txt
    echo "ROUTE_TABLE_ID=$ROUTE_TABLE_ID" >> aws-config.txt
}

# Function to create security groups
create_security_groups() {
    print_status "Creating security groups..."
    
    # Load VPC ID
    source aws-config.txt
    
    # Create Application Security Group
    APP_SG_ID=$(aws ec2 create-security-group \
        --group-name $PROJECT_NAME-app-sg \
        --description "Security group for TimeSheet application" \
        --vpc-id $VPC_ID \
        --query 'GroupId' --output text)
    
    # Create Database Security Group
    DB_SG_ID=$(aws ec2 create-security-group \
        --group-name $PROJECT_NAME-db-sg \
        --description "Security group for MySQL database" \
        --vpc-id $VPC_ID \
        --query 'GroupId' --output text)
    
    # Configure Application Security Group
    aws ec2 authorize-security-group-ingress \
        --group-id $APP_SG_ID \
        --protocol tcp \
        --port 22 \
        --cidr 0.0.0.0/0
    
    aws ec2 authorize-security-group-ingress \
        --group-id $APP_SG_ID \
        --protocol tcp \
        --port 80 \
        --cidr 0.0.0.0/0
    
    aws ec2 authorize-security-group-ingress \
        --group-id $APP_SG_ID \
        --protocol tcp \
        --port 443 \
        --cidr 0.0.0.0/0
    
    aws ec2 authorize-security-group-ingress \
        --group-id $APP_SG_ID \
        --protocol tcp \
        --port 8080 \
        --cidr 0.0.0.0/0
    
    # Configure Database Security Group
    aws ec2 authorize-security-group-ingress \
        --group-id $DB_SG_ID \
        --protocol tcp \
        --port 3306 \
        --source-group $APP_SG_ID
    
    print_success "Security groups created successfully"
    echo "APP_SG_ID=$APP_SG_ID" >> aws-config.txt
    echo "DB_SG_ID=$DB_SG_ID" >> aws-config.txt
}

# Function to create EC2 instance
create_ec2_instance() {
    print_status "Creating EC2 instance..."
    
    source aws-config.txt
    
    # Get latest Amazon Linux 2 AMI
    AMI_ID=$(aws ssm get-parameters --names /aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2 --query 'Parameters[0].Value' --output text)
    
    # Create EC2 instance
    INSTANCE_ID=$(aws ec2 run-instances \
        --image-id $AMI_ID \
        --count 1 \
        --instance-type $INSTANCE_TYPE \
        --key-name $KEY_NAME \
        --security-group-ids $APP_SG_ID \
        --subnet-id $SUBNET_ID \
        --user-data file://aws-user-data.sh \
        --query 'Instances[0].InstanceId' --output text)
    
    aws ec2 create-tags --resources $INSTANCE_ID --tags Key=Name,Value=$PROJECT_NAME-app
    
    # Wait for instance to be running
    print_status "Waiting for instance to be running..."
    aws ec2 wait instance-running --instance-ids $INSTANCE_ID
    
    # Get public IP
    PUBLIC_IP=$(aws ec2 describe-instances --instance-ids $INSTANCE_ID --query 'Reservations[0].Instances[0].PublicIpAddress' --output text)
    
    print_success "EC2 instance created successfully"
    echo "INSTANCE_ID=$INSTANCE_ID" >> aws-config.txt
    echo "PUBLIC_IP=$PUBLIC_IP" >> aws-config.txt
}

# Function to create RDS instance
create_rds_instance() {
    print_status "Creating RDS MySQL instance..."
    
    source aws-config.txt
    
    # Create RDS subnet group
    aws rds create-db-subnet-group \
        --db-subnet-group-name $PROJECT_NAME-subnet-group \
        --db-subnet-group-description "Subnet group for TimeSheet database" \
        --subnet-ids $SUBNET_ID
    
    # Create RDS instance
    DB_INSTANCE_ID=$(aws rds create-db-instance \
        --db-instance-identifier $PROJECT_NAME-db \
        --db-instance-class $DB_INSTANCE_CLASS \
        --engine mysql \
        --engine-version 8.0.35 \
        --master-username admin \
        --master-user-password TimesheetDB123! \
        --allocated-storage 20 \
        --storage-type gp2 \
        --vpc-security-group-ids $DB_SG_ID \
        --db-subnet-group-name $PROJECT_NAME-subnet-group \
        --backup-retention-period 7 \
        --preferred-backup-window "03:00-04:00" \
        --preferred-maintenance-window "sun:04:00-sun:05:00" \
        --query 'DBInstance.DBInstanceIdentifier' --output text)
    
    print_status "Waiting for RDS instance to be available..."
    aws rds wait db-instance-available --db-instance-identifier $DB_INSTANCE_ID
    
    # Get RDS endpoint
    DB_ENDPOINT=$(aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_ID --query 'DBInstances[0].Endpoint.Address' --output text)
    
    print_success "RDS instance created successfully"
    echo "DB_INSTANCE_ID=$DB_INSTANCE_ID" >> aws-config.txt
    echo "DB_ENDPOINT=$DB_ENDPOINT" >> aws-config.txt
}

# Function to initialize database with schema and test data
initialize_database() {
    print_status "Initializing database with schema and test data..."
    
    source aws-config.txt
    
    # Wait a bit more for RDS to be fully ready
    sleep 30
    
    # Create database initialization script
    cat > init-database.sh << 'EOF'
#!/bin/bash

# Database initialization script
DB_ENDPOINT="$1"
DB_USER="admin"
DB_PASSWORD="TimesheetDB123!"

echo "Connecting to database at $DB_ENDPOINT..."

# Wait for database to be ready
until mysql -h "$DB_ENDPOINT" -u "$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1;" > /dev/null 2>&1; do
    echo "Waiting for database to be ready..."
    sleep 10
done

echo "Database is ready. Creating database and initializing..."

# Create database if it doesn't exist
mysql -h "$DB_ENDPOINT" -u "$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS timesheetdb;"

# Run test data script
mysql -h "$DB_ENDPOINT" -u "$DB_USER" -p"$DB_PASSWORD" timesheetdb < create_test_data.sql

echo "Database initialization completed successfully!"
EOF

    chmod +x init-database.sh
    
    # Copy initialization script to EC2 instance
    scp -i ~/.ssh/$KEY_NAME.pem -o StrictHostKeyChecking=no init-database.sh ec2-user@$PUBLIC_IP:~/
    scp -i ~/.ssh/$KEY_NAME.pem -o StrictHostKeyChecking=no create_test_data.sql ec2-user@$PUBLIC_IP:~/
    
    # Execute database initialization
    ssh -i ~/.ssh/$KEY_NAME.pem -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP << EOF
        # Install MySQL client
        sudo yum install -y mysql
        
        # Run database initialization
        ./init-database.sh $DB_ENDPOINT
        
        # Clean up
        rm -f init-database.sh create_test_data.sql
EOF
    
    # Clean up local files
    rm -f init-database.sh
    
    print_success "Database initialized successfully with schema and test data"
}

# Function to deploy application
deploy_application() {
    print_status "Deploying application to EC2..."
    
    source aws-config.txt
    
    # Wait for instance to be ready
    print_status "Waiting for instance to be ready..."
    sleep 60
    
    # Copy application files
    print_status "Copying application files..."
    scp -i ~/.ssh/$KEY_NAME.pem -o StrictHostKeyChecking=no -r . ec2-user@$PUBLIC_IP:~/TimeSheetManagement/
    
    # Execute deployment commands
    print_status "Executing deployment commands..."
    ssh -i ~/.ssh/$KEY_NAME.pem -o StrictHostKeyChecking=no ec2-user@$PUBLIC_IP << 'EOF'
        cd ~/TimeSheetManagement
        
        # Install Docker and Docker Compose
        sudo yum update -y
        sudo yum install -y docker
        sudo systemctl start docker
        sudo systemctl enable docker
        sudo usermod -a -G docker ec2-user
        
        # Install Docker Compose
        sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
        
        # Create production environment file
        cat > .env << 'ENVEOF'
MYSQL_ROOT_PASSWORD=TimesheetDB123!
MYSQL_USER=timesheet_user
MYSQL_PASSWORD=TimesheetUser123!
JWT_SECRET=your-super-secure-jwt-secret-key-here-make-it-long-and-secure-for-production-use-at-least-512-bits-for-hs512-algorithm
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://DB_ENDPOINT:3306/timesheetdb?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=TimesheetDB123!
ENVEOF
        
        # Replace DB_ENDPOINT placeholder
        sed -i "s/DB_ENDPOINT/$DB_ENDPOINT/g" .env
        
        # Deploy with Docker Compose
        docker-compose -f docker-compose.prod.yml up --build -d
        
        # Wait for services to be ready
        sleep 30
        
        # Check if application is running
        if curl -f http://localhost:8080/api/health; then
            echo "Application deployed successfully!"
        else
            echo "Application deployment failed!"
            exit 1
        fi
EOF
    
    print_success "Application deployed successfully!"
    print_status "Your application is now running at: http://$PUBLIC_IP:8080"
}

# Function to create SSL certificate (optional)
create_ssl_certificate() {
    print_status "Creating SSL certificate..."
    
    source aws-config.txt
    
    # Request SSL certificate
    CERT_ARN=$(aws acm request-certificate \
        --domain-name $PUBLIC_IP.nip.io \
        --validation-method DNS \
        --query 'CertificateArn' --output text)
    
    print_status "SSL certificate requested. You'll need to validate it manually."
    echo "CERT_ARN=$CERT_ARN" >> aws-config.txt
}

# Function to show deployment status
show_status() {
    print_status "Checking deployment status..."
    
    if [ -f aws-config.txt ]; then
        source aws-config.txt
        
        # Check EC2 instance status
        INSTANCE_STATE=$(aws ec2 describe-instances --instance-ids $INSTANCE_ID --query 'Reservations[0].Instances[0].State.Name' --output text)
        print_status "EC2 Instance ($INSTANCE_ID): $INSTANCE_STATE"
        
        # Check RDS instance status
        DB_STATE=$(aws rds describe-db-instances --db-instance-identifier $DB_INSTANCE_ID --query 'DBInstances[0].DBInstanceStatus' --output text)
        print_status "RDS Instance ($DB_INSTANCE_ID): $DB_STATE"
        
        # Check application health
        if curl -f http://$PUBLIC_IP:8080/api/health &> /dev/null; then
            print_success "Application is running at http://$PUBLIC_IP:8080"
        else
            print_warning "Application is not responding"
        fi
    else
        print_warning "No deployment configuration found"
    fi
}

# Function to clean up resources
cleanup() {
    print_status "Cleaning up AWS resources..."
    
    if [ -f aws-config.txt ]; then
        source aws-config.txt
        
        # Terminate EC2 instance
        if [ ! -z "$INSTANCE_ID" ]; then
            aws ec2 terminate-instances --instance-ids $INSTANCE_ID
            aws ec2 wait instance-terminated --instance-ids $INSTANCE_ID
        fi
        
        # Delete RDS instance
        if [ ! -z "$DB_INSTANCE_ID" ]; then
            aws rds delete-db-instance --db-instance-identifier $DB_INSTANCE_ID --skip-final-snapshot
            aws rds wait db-instance-deleted --db-instance-identifier $DB_INSTANCE_ID
        fi
        
        # Delete security groups
        if [ ! -z "$APP_SG_ID" ]; then
            aws ec2 delete-security-group --group-id $APP_SG_ID
        fi
        
        if [ ! -z "$DB_SG_ID" ]; then
            aws ec2 delete-security-group --group-id $DB_SG_ID
        fi
        
        # Delete VPC
        if [ ! -z "$VPC_ID" ]; then
            aws ec2 delete-vpc --vpc-id $VPC_ID
        fi
        
        # Remove config file
        rm -f aws-config.txt
        
        print_success "All resources cleaned up successfully"
    else
        print_warning "No deployment configuration found"
    fi
}

# Main deployment function
deploy_to_aws() {
    print_status "Starting AWS deployment..."
    
    check_aws_cli
    
    # Check if key pair exists
    if ! aws ec2 describe-key-pairs --key-names $KEY_NAME &> /dev/null; then
        print_error "Key pair '$KEY_NAME' not found. Please create it first:"
        print_status "aws ec2 create-key-pair --key-name $KEY_NAME --query 'KeyMaterial' --output text > ~/.ssh/$KEY_NAME.pem"
        print_status "chmod 400 ~/.ssh/$KEY_NAME.pem"
        exit 1
    fi
    
    create_networking
    create_security_groups
    create_ec2_instance
    create_rds_instance
    initialize_database
    deploy_application
    
    print_success "AWS deployment completed successfully!"
    print_status "Application URL: http://$PUBLIC_IP:8080"
    print_status "Database Endpoint: $DB_ENDPOINT"
    print_status "Configuration saved in: aws-config.txt"
}

# Main script
main() {
    echo "=========================================="
    echo "AWS Deployment for TimeSheet Management"
    echo "=========================================="
    echo ""
    echo "Options:"
    echo "1. Deploy to AWS (Full deployment)"
    echo "2. Show deployment status"
    echo "3. Clean up resources"
    echo "4. Exit"
    echo ""
    
    read -p "Select option (1-4): " choice
    
    case $choice in
        1) deploy_to_aws ;;
        2) show_status ;;
        3) cleanup ;;
        4) print_status "Goodbye!"; exit 0 ;;
        *) print_error "Invalid option"; main ;;
    esac
}

# Run main function
main
