# AWS Deployment Guide for TimeSheet Management System

This guide provides detailed instructions for deploying the TimeSheet Management System to AWS using EC2 and RDS.

## 🏗️ Architecture Overview

```
Internet
    ↓
EC2 Instance (t3.medium)
├── Spring Boot Application (Port 8080)
├── Docker & Docker Compose
└── Nginx (Optional - Port 80/443)
    ↓
RDS MySQL Instance (db.t3.micro)
└── Database (Port 3306)
```

## 📋 Prerequisites

### 1. AWS Account Setup
- AWS account with billing enabled
- IAM user with appropriate permissions
- AWS CLI installed and configured

### 2. Required Permissions
Your IAM user needs the following permissions:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:*",
                "rds:*",
                "iam:CreateRole",
                "iam:AttachRolePolicy",
                "iam:CreateInstanceProfile",
                "iam:AddRoleToInstanceProfile"
            ],
            "Resource": "*"
        }
    ]
}
```

### 3. Local Setup
```bash
# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Configure AWS CLI
aws configure
# Enter your AWS Access Key ID
# Enter your AWS Secret Access Key
# Enter your default region (e.g., us-east-1)
# Enter your output format (json)
```

## 🚀 Quick Deployment

### Option 1: Automated Deployment (Recommended)

```bash
# Make the deployment script executable
chmod +x aws-deploy.sh

# Run the deployment script
./aws-deploy.sh
# Select option 1: Deploy to AWS (Full deployment)
```

### Option 2: Manual Deployment

Follow the step-by-step instructions below.

## 📝 Step-by-Step Manual Deployment

### Step 1: Create Key Pair

```bash
# Create a key pair for SSH access
aws ec2 create-key-pair \
    --key-name timesheet-key \
    --query 'KeyMaterial' \
    --output text > ~/.ssh/timesheet-key.pem

# Set proper permissions
chmod 400 ~/.ssh/timesheet-key.pem
```

### Step 2: Create VPC and Networking

```bash
# Create VPC
VPC_ID=$(aws ec2 create-vpc \
    --cidr-block 10.0.0.0/16 \
    --query 'Vpc.VpcId' \
    --output text)

# Tag the VPC
aws ec2 create-tags \
    --resources $VPC_ID \
    --tags Key=Name,Value=timesheet-vpc

# Create Internet Gateway
IGW_ID=$(aws ec2 create-internet-gateway \
    --query 'InternetGateway.InternetGatewayId' \
    --output text)

# Attach Internet Gateway to VPC
aws ec2 attach-internet-gateway \
    --vpc-id $VPC_ID \
    --internet-gateway-id $IGW_ID

# Create Subnet
SUBNET_ID=$(aws ec2 create-subnet \
    --vpc-id $VPC_ID \
    --cidr-block 10.0.1.0/24 \
    --query 'Subnet.SubnetId' \
    --output text)

# Create Route Table
ROUTE_TABLE_ID=$(aws ec2 create-route-table \
    --vpc-id $VPC_ID \
    --query 'RouteTable.RouteTableId' \
    --output text)

# Add route to Internet Gateway
aws ec2 create-route \
    --route-table-id $ROUTE_TABLE_ID \
    --destination-cidr-block 0.0.0.0/0 \
    --gateway-id $IGW_ID

# Associate route table with subnet
aws ec2 associate-route-table \
    --subnet-id $SUBNET_ID \
    --route-table-id $ROUTE_TABLE_ID
```

### Step 3: Create Security Groups

```bash
# Create Application Security Group
APP_SG_ID=$(aws ec2 create-security-group \
    --group-name timesheet-app-sg \
    --description "Security group for TimeSheet application" \
    --vpc-id $VPC_ID \
    --query 'GroupId' \
    --output text)

# Create Database Security Group
DB_SG_ID=$(aws ec2 create-security-group \
    --group-name timesheet-db-sg \
    --description "Security group for MySQL database" \
    --vpc-id $VPC_ID \
    --query 'GroupId' \
    --output text)

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
```

### Step 4: Create RDS Instance

```bash
# Create RDS subnet group
aws rds create-db-subnet-group \
    --db-subnet-group-name timesheet-subnet-group \
    --db-subnet-group-description "Subnet group for TimeSheet database" \
    --subnet-ids $SUBNET_ID

# Create RDS instance
DB_INSTANCE_ID=$(aws rds create-db-instance \
    --db-instance-identifier timesheet-db \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --engine-version 8.0.35 \
    --master-username admin \
    --master-user-password TimesheetDB123! \
    --allocated-storage 20 \
    --storage-type gp2 \
    --vpc-security-group-ids $DB_SG_ID \
    --db-subnet-group-name timesheet-subnet-group \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00" \
    --preferred-maintenance-window "sun:04:00-sun:05:00" \
    --query 'DBInstance.DBInstanceIdentifier' \
    --output text)

# Wait for RDS to be available
aws rds wait db-instance-available \
    --db-instance-identifier $DB_INSTANCE_ID

# Get RDS endpoint
DB_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier $DB_INSTANCE_ID \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)
```

### Step 5: Create EC2 Instance

```bash
# Get latest Amazon Linux 2 AMI
AMI_ID=$(aws ssm get-parameters \
    --names /aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2 \
    --query 'Parameters[0].Value' \
    --output text)

# Create EC2 instance
INSTANCE_ID=$(aws ec2 run-instances \
    --image-id $AMI_ID \
    --count 1 \
    --instance-type t3.medium \
    --key-name timesheet-key \
    --security-group-ids $APP_SG_ID \
    --subnet-id $SUBNET_ID \
    --user-data file://aws-user-data.sh \
    --query 'Instances[0].InstanceId' \
    --output text)

# Wait for instance to be running
aws ec2 wait instance-running \
    --instance-ids $INSTANCE_ID

# Get public IP
PUBLIC_IP=$(aws ec2 describe-instances \
    --instance-ids $INSTANCE_ID \
    --query 'Reservations[0].Instances[0].PublicIpAddress' \
    --output text)
```

### Step 6: Initialize Database

```bash
# Wait for RDS to be fully ready
sleep 30

# Copy database initialization files
scp -i ~/.ssh/timesheet-key.pem \
    -o StrictHostKeyChecking=no \
    init-database.sh create_test_data.sql ec2-user@$PUBLIC_IP:~/

# Initialize database with schema and test data
ssh -i ~/.ssh/timesheet-key.pem \
    -o StrictHostKeyChecking=no \
    ec2-user@$PUBLIC_IP << EOF
# Install MySQL client
sudo yum install -y mysql

# Make script executable
chmod +x init-database.sh

# Initialize database
./init-database.sh -h $DB_ENDPOINT -u admin -w TimesheetDB123! -d timesheetdb

# Clean up
rm -f init-database.sh create_test_data.sql
EOF
```

### Step 7: Deploy Application

```bash
# Copy application files
scp -i ~/.ssh/timesheet-key.pem \
    -o StrictHostKeyChecking=no \
    -r . ec2-user@$PUBLIC_IP:~/TimeSheetManagement/

# Create environment file and deploy
ssh -i ~/.ssh/timesheet-key.pem \
    -o StrictHostKeyChecking=no \
    ec2-user@$PUBLIC_IP << EOF
cd ~/TimeSheetManagement

# Create .env file
cat > .env << 'ENVEOF'
MYSQL_ROOT_PASSWORD=TimesheetDB123!
MYSQL_USER=timesheet_user
MYSQL_PASSWORD=TimesheetUser123!
JWT_SECRET=your-super-secure-jwt-secret-key-here-make-it-long-and-secure-for-production-use-at-least-512-bits-for-hs512-algorithm
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://$DB_ENDPOINT:3306/timesheetdb?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=TimesheetDB123!
ENVEOF

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
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file on the EC2 instance:

```bash
# Database Configuration
MYSQL_ROOT_PASSWORD=TimesheetDB123!
MYSQL_USER=timesheet_user
MYSQL_PASSWORD=TimesheetUser123!

# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-here-make-it-long-and-secure-for-production-use-at-least-512-bits-for-hs512-algorithm

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Database Connection
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/timesheetdb?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=TimesheetDB123!
```

### Security Considerations

1. **Change Default Passwords**
   - Update database passwords
   - Use strong JWT secrets
   - Rotate keys regularly

2. **Network Security**
   - Restrict SSH access to your IP
   - Use VPC for network isolation
   - Enable SSL for database connections

3. **Application Security**
   - Enable HTTPS
   - Configure CORS properly
   - Implement rate limiting

## 📊 Monitoring and Maintenance

### Health Checks

```bash
# Check application health
curl http://your-ec2-ip:8080/api/health

# Check database connectivity
ssh -i ~/.ssh/timesheet-key.pem ec2-user@your-ec2-ip
docker-compose -f ~/TimeSheetManagement/docker-compose.prod.yml exec mysql mysqladmin ping
```

### Logs

```bash
# View application logs
ssh -i ~/.ssh/timesheet-key.pem ec2-user@your-ec2-ip
docker-compose -f ~/TimeSheetManagement/docker-compose.prod.yml logs -f timesheet-app

# View system logs
sudo journalctl -u timesheet.service -f
```

### Backups

```bash
# Create database backup
ssh -i ~/.ssh/timesheet-key.pem ec2-user@your-ec2-ip
docker-compose -f ~/TimeSheetManagement/docker-compose.prod.yml exec mysql mysqldump -u root -pTimesheetDB123! timesheetdb > backup.sql

# Restore database
docker-compose -f ~/TimeSheetManagement/docker-compose.prod.yml exec -T mysql mysql -u root -pTimesheetDB123! timesheetdb < backup.sql
```

## 💰 Cost Optimization

### Estimated Monthly Costs (us-east-1)

- **EC2 t3.medium**: ~$30/month
- **RDS db.t3.micro**: ~$15/month
- **Data Transfer**: ~$5/month
- **Total**: ~$50/month

### Cost Reduction Tips

1. **Use Reserved Instances** for 1-3 year commitments
2. **Stop instances** when not in use (dev/test)
3. **Use Spot Instances** for non-critical workloads
4. **Enable RDS storage autoscaling**
5. **Monitor and optimize resource usage**

## 🚨 Troubleshooting

### Common Issues

1. **Application Won't Start**
   ```bash
   # Check Docker containers
   docker ps -a
   
   # Check logs
   docker-compose logs timesheet-app
   
   # Check environment variables
   cat .env
   ```

2. **Database Connection Failed**
   ```bash
   # Check RDS status
   aws rds describe-db-instances --db-instance-identifier timesheet-db
   
   # Test connection
   mysql -h your-rds-endpoint -u admin -p
   ```

3. **Security Group Issues**
   ```bash
   # Check security group rules
   aws ec2 describe-security-groups --group-ids $APP_SG_ID
   aws ec2 describe-security-groups --group-ids $DB_SG_ID
   ```

### Performance Issues

1. **High CPU Usage**
   - Upgrade instance type
   - Optimize application code
   - Add caching layer

2. **High Memory Usage**
   - Increase instance memory
   - Optimize JVM settings
   - Monitor memory leaks

3. **Database Performance**
   - Add database indexes
   - Optimize queries
   - Consider read replicas

## 🔄 Updates and Scaling

### Application Updates

```bash
# Pull latest changes
ssh -i ~/.ssh/timesheet-key.pem ec2-user@your-ec2-ip
cd ~/TimeSheetManagement
git pull origin main

# Rebuild and restart
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up --build -d
```

### Scaling Options

1. **Vertical Scaling**
   - Upgrade EC2 instance type
   - Upgrade RDS instance class

2. **Horizontal Scaling**
   - Use Application Load Balancer
   - Deploy multiple EC2 instances
   - Use RDS read replicas

3. **Auto Scaling**
   - Configure Auto Scaling Group
   - Set up CloudWatch alarms
   - Use target tracking policies

## 🧹 Cleanup

### Remove All Resources

```bash
# Use the cleanup function in the deployment script
./aws-deploy.sh
# Select option 3: Clean up resources
```

Or manually:
```bash
# Terminate EC2 instance
aws ec2 terminate-instances --instance-ids $INSTANCE_ID

# Delete RDS instance
aws rds delete-db-instance \
    --db-instance-identifier timesheet-db \
    --skip-final-snapshot

# Delete security groups
aws ec2 delete-security-group --group-id $APP_SG_ID
aws ec2 delete-security-group --group-id $DB_SG_ID

# Delete VPC
aws ec2 delete-vpc --vpc-id $VPC_ID
```

## 📞 Support

For AWS deployment issues:
1. Check AWS CloudWatch logs
2. Review EC2 instance logs
3. Verify security group configurations
4. Test network connectivity
5. Check AWS service limits

## 🔗 Useful Links

- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS RDS Documentation](https://docs.aws.amazon.com/rds/)
- [AWS CLI Documentation](https://docs.aws.amazon.com/cli/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
