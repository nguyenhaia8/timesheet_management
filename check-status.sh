#!/bin/bash

# TimeSheet Management Status Check Script

echo "🔍 Checking TimeSheet Management Deployment Status..."

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

echo ""
echo "📊 Pod Status:"
kubectl get pods -n timesheet-management

echo ""
echo "🌐 Service Status:"
kubectl get service -n timesheet-management

echo ""
echo "🔗 External IP:"
EXTERNAL_IP=$(kubectl get service timesheet-https-8080 -n timesheet-management -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "Not assigned")

if [ ! -z "$EXTERNAL_IP" ] && [ "$EXTERNAL_IP" != "Not assigned" ]; then
    print_status "HTTPS Backend URL: https://$EXTERNAL_IP:8080"
    
    echo ""
    echo "🧪 Testing HTTPS endpoint..."
    if curl -k -s -o /dev/null -w "%{http_code}" https://$EXTERNAL_IP:8080/api/auth/health | grep -q "200"; then
        print_status "Health check: ✅ Working"
    else
        print_warning "Health check: ⚠️  Failed"
    fi
    
    echo ""
    echo "🔑 Testing login..."
    LOGIN_RESPONSE=$(curl -k -s -X POST https://$EXTERNAL_IP:8080/api/auth/login -H "Content-Type: application/json" -d '{"userName":"admin","password":"password"}')
    if echo "$LOGIN_RESPONSE" | grep -q "success.*true"; then
        print_status "Login: ✅ Working"
    else
        print_warning "Login: ⚠️  Failed - $LOGIN_RESPONSE"
    fi
else
    print_warning "External IP not assigned yet"
fi

echo ""
echo "📱 Frontend URL: https://timesheet-fe-psi.vercel.app/"
echo ""
echo "🔧 Troubleshooting Commands:"
echo "   kubectl logs [POD_NAME] -n timesheet-management"
echo "   kubectl describe pod [POD_NAME] -n timesheet-management"
echo "   kubectl get events -n timesheet-management --sort-by='.lastTimestamp'"
