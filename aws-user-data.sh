#!/bin/bash

# User data script for EC2 instance initialization
# This script runs when the EC2 instance first starts

# Update system
yum update -y

# Install essential packages
yum install -y \
    java-21-amazon-corretto \
    maven \
    git \
    curl \
    wget \
    unzip \
    docker \
    docker-compose

# Start and enable Docker
systemctl start docker
systemctl enable docker

# Add ec2-user to docker group
usermod -a -G docker ec2-user

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Create application directory
mkdir -p /home/ec2-user/TimeSheetManagement
chown ec2-user:ec2-user /home/ec2-user/TimeSheetManagement

# Set up logging
mkdir -p /var/log/timesheet
touch /var/log/timesheet/application.log
chown ec2-user:ec2-user /var/log/timesheet/application.log

# Create systemd service for application (optional)
cat > /etc/systemd/system/timesheet.service << 'EOF'
[Unit]
Description=TimeSheet Management System
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ec2-user/TimeSheetManagement
ExecStart=/usr/local/bin/docker-compose -f docker-compose.prod.yml up -d
ExecStop=/usr/local/bin/docker-compose -f docker-compose.prod.yml down
User=ec2-user
Group=ec2-user

[Install]
WantedBy=multi-user.target
EOF

# Enable the service
systemctl enable timesheet.service

# Create health check script
cat > /home/ec2-user/health-check.sh << 'EOF'
#!/bin/bash
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "Application is healthy"
    exit 0
else
    echo "Application is not responding"
    exit 1
fi
EOF

chmod +x /home/ec2-user/health-check.sh
chown ec2-user:ec2-user /home/ec2-user/health-check.sh

# Set up log rotation
cat > /etc/logrotate.d/timesheet << 'EOF'
/var/log/timesheet/*.log {
    daily
    missingok
    rotate 7
    compress
    delaycompress
    notifempty
    create 644 ec2-user ec2-user
}
EOF

# Create monitoring script
cat > /home/ec2-user/monitor.sh << 'EOF'
#!/bin/bash

# Simple monitoring script
echo "=== TimeSheet Management System Status ==="
echo "Date: $(date)"
echo ""

# Check Docker containers
echo "Docker Containers:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""

# Check application health
echo "Application Health:"
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Application is running"
else
    echo "❌ Application is not responding"
fi
echo ""

# Check disk usage
echo "Disk Usage:"
df -h /
echo ""

# Check memory usage
echo "Memory Usage:"
free -h
echo ""

# Check system load
echo "System Load:"
uptime
EOF

chmod +x /home/ec2-user/monitor.sh
chown ec2-user:ec2-user /home/ec2-user/monitor.sh

# Set up cron job for monitoring
echo "*/5 * * * * /home/ec2-user/health-check.sh >> /var/log/timesheet/health.log 2>&1" | crontab -u ec2-user -

# Create backup script
cat > /home/ec2-user/backup.sh << 'EOF'
#!/bin/bash

# Backup script for TimeSheet Management System
BACKUP_DIR="/home/ec2-user/backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup database
docker-compose -f /home/ec2-user/TimeSheetManagement/docker-compose.prod.yml exec -T mysql mysqldump -u root -pTimesheetDB123! timesheetdb > $BACKUP_DIR/db_backup_$DATE.sql

# Backup application logs
tar -czf $BACKUP_DIR/logs_backup_$DATE.tar.gz /var/log/timesheet/

# Keep only last 7 days of backups
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
EOF

chmod +x /home/ec2-user/backup.sh
chown ec2-user:ec2-user /home/ec2-user/backup.sh

# Set up daily backup
echo "0 2 * * * /home/ec2-user/backup.sh >> /var/log/timesheet/backup.log 2>&1" | crontab -u ec2-user -

# Create environment file template
cat > /home/ec2-user/TimeSheetManagement/.env.template << 'EOF'
# Database Configuration
MYSQL_ROOT_PASSWORD=TimesheetDB123!
MYSQL_USER=timesheet_user
MYSQL_PASSWORD=TimesheetUser123!

# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-here-make-it-long-and-secure-for-production-use-at-least-512-bits-for-hs512-algorithm

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Database Connection (will be updated during deployment)
SPRING_DATASOURCE_URL=jdbc:mysql://DB_ENDPOINT:3306/timesheetdb?useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=TimesheetDB123!
EOF

chown ec2-user:ec2-user /home/ec2-user/TimeSheetManagement/.env.template

# Print completion message
echo "EC2 instance initialization completed at $(date)" > /var/log/timesheet/init.log
