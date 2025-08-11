#!/bin/bash

# TimeSheet Management - Clean HTTPS Deployment
# Deploys the backend with HTTPS at https://128.203.177.65:8080

set -e

echo "🚀 Deploying TimeSheet Management with Clean HTTPS Setup..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed or not in PATH"
    exit 1
fi

# Check if docker is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi

print_status "Building Docker image..."
docker build --platform linux/amd64 -t timesheetacr.azurecr.io/timesheet-app:latest .

print_status "Pushing Docker image to Azure Container Registry..."
docker push timesheetacr.azurecr.io/timesheet-app:latest

print_status "Cleaning up old deployments..."
# Delete old deployments and services
kubectl delete deployment timesheet-mysql -n timesheet-management --ignore-not-found=true
kubectl delete deployment timesheet-app -n timesheet-management --ignore-not-found=true
kubectl delete deployment nginx-https-direct -n timesheet-management --ignore-not-found=true
kubectl delete service nginx-https-direct-service -n timesheet-management --ignore-not-found=true

print_status "Deploying to Kubernetes..."
kubectl apply -k k8s/

print_status "Waiting for MySQL deployment to be ready..."
kubectl rollout status deployment/timesheet-mysql -n timesheet-management --timeout=300s

print_status "Waiting for application deployment to be ready..."
kubectl rollout status deployment/timesheet-app -n timesheet-management --timeout=300s

print_status "Waiting for Nginx HTTPS proxy to be ready..."
kubectl rollout status deployment/nginx-https-direct -n timesheet-management --timeout=300s

print_status "Waiting for HTTPS service to get external IP..."
echo "This may take a few minutes..."

# Wait for external IP
for i in {1..30}; do
    EXTERNAL_IP=$(kubectl get service timesheet-https-8080 -n timesheet-management -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "")
    if [ ! -z "$EXTERNAL_IP" ]; then
        break
    fi
    echo "Waiting for external IP... (attempt $i/30)"
    sleep 10
done

if [ -z "$EXTERNAL_IP" ]; then
    print_warning "External IP not assigned yet. You can check with: kubectl get service timesheet-https-8080 -n timesheet-management"
else
    print_status "External IP assigned: $EXTERNAL_IP"
fi

print_status "Testing HTTPS endpoint..."
sleep 15

# Test the endpoint
if curl -k -s -o /dev/null -w "%{http_code}" https://$EXTERNAL_IP:8080/api/auth/health | grep -q "200"; then
    print_status "HTTPS endpoint is working!"
    
    # Test login
    LOGIN_RESPONSE=$(curl -k -s -X POST https://$EXTERNAL_IP:8080/api/auth/login -H "Content-Type: application/json" -d '{"userName":"admin","password":"password"}')
    if echo "$LOGIN_RESPONSE" | grep -q "success.*true\|token"; then
        print_status "Login endpoint is working!"
    else
        print_warning "Login endpoint test failed - $LOGIN_RESPONSE"
    fi
    
    echo ""
    echo "🎉 Deployment successful!"
    echo "📱 Frontend URL: https://timesheet-fe-psi.vercel.app/"
    echo "🔗 Backend URL: https://$EXTERNAL_IP:8080"
    echo ""
    echo "🔑 Test credentials:"
    echo "   Username: admin"
    echo "   Password: password"
    echo ""
    echo "📋 Test commands:"
    echo "   curl -k -X GET https://$EXTERNAL_IP:8080/api/auth/health"
    echo "   curl -k -X POST https://$EXTERNAL_IP:8080/api/auth/login -H 'Content-Type: application/json' -d '{\"userName\":\"admin\",\"password\":\"password\"}'"
    echo "   curl -k -X GET https://$EXTERNAL_IP:8080/api/employees"
    echo "   curl -k -X GET https://$EXTERNAL_IP:8080/api/departments"
else
    print_warning "HTTPS endpoint test failed. The service might still be starting up."
    echo "You can check the status with: kubectl get pods -n timesheet-management"
fi

print_status "Deployment completed!"
