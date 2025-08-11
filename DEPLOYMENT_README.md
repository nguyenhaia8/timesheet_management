# TimeSheet Management - HTTPS Deployment

This project deploys a TimeSheet Management backend with HTTPS support using Docker and Kubernetes.

## 🚀 Quick Deployment

### Prerequisites
- Docker installed and running
- kubectl configured for your Kubernetes cluster
- Azure Container Registry (ACR) access

### Deploy with HTTPS
```bash
./deploy-https.sh
```

## 📋 Deployment Architecture

### Components
1. **Spring Boot Application**: Main backend service
2. **MySQL Database**: Persistent database storage
3. **Nginx HTTPS Proxy**: SSL termination and CORS handling
4. **Cert-Manager**: SSL certificate management

### Services
- **Backend URL**: `https://[EXTERNAL_IP]:8080`
- **Frontend URL**: `https://timesheet-fe-psi.vercel.app/`

## 🔧 Configuration Files

### Core Files
- `k8s/namespace.yaml` - Kubernetes namespace
- `k8s/secret.yaml` - Database and JWT secrets
- `k8s/configmap.yaml` - Application configuration
- `k8s/app-deployment.yaml` - Spring Boot application deployment
- `k8s/mysql-deployment.yaml` - MySQL database deployment
- `k8s/nginx-https-direct.yaml` - Nginx HTTPS proxy
- `k8s/https-8080-replacement.yaml` - HTTPS service on port 8080
- `k8s/cluster-issuer.yaml` - SSL certificate configuration

### Deployment Script
- `deploy-https.sh` - Automated deployment script

## 🔑 Test Credentials

### Admin User
- **Username**: `admin`
- **Password**: `password`
- **Role**: `ROLE_ADMIN`

### Manager User
- **Username**: `manager`
- **Password**: `password`
- **Role**: `ROLE_MANAGER`

### Employee User
- **Username**: `employee1`
- **Password**: `password`
- **Role**: `ROLE_EMPLOYEE`

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `GET /api/auth/health` - Health check

### Public Endpoints (No Auth Required)
- `GET /api/employees` - List all employees
- `GET /api/departments` - List all departments
- `GET /api/projects` - List all projects

### Protected Endpoints (Require JWT Token)
- `GET /api/timesheets` - List timesheets
- `POST /api/timesheets` - Create timesheet
- `PUT /api/timesheets/{id}` - Update timesheet
- `DELETE /api/timesheets/{id}` - Delete timesheet

## 🛠️ Manual Deployment Steps

1. **Build and Push Docker Image**
   ```bash
   docker build --platform linux/amd64 -t timesheetacr.azurecr.io/timesheet-app:latest .
   docker push timesheetacr.azurecr.io/timesheet-app:latest
   ```

2. **Deploy to Kubernetes**
   ```bash
   kubectl apply -k k8s/
   ```

3. **Check Deployment Status**
   ```bash
   kubectl get pods -n timesheet-management
   kubectl get service timesheet-https-8080 -n timesheet-management
   ```

4. **Test HTTPS Endpoint**
   ```bash
   curl -k -X GET https://[EXTERNAL_IP]:8080/api/auth/health
   ```

## 🔍 Troubleshooting

### Check Pod Status
```bash
kubectl get pods -n timesheet-management
kubectl describe pod [POD_NAME] -n timesheet-management
```

### Check Service Status
```bash
kubectl get service -n timesheet-management
kubectl describe service timesheet-https-8080 -n timesheet-management
```

### Check Logs
```bash
kubectl logs [POD_NAME] -n timesheet-management
```

### Check Certificate Status
```bash
kubectl get certificate -n timesheet-management
kubectl describe certificate timesheet-cert -n timesheet-management
```

## 🧹 Cleanup

To remove the deployment:
```bash
kubectl delete -k k8s/
```

## 📝 Notes

- The deployment uses a self-signed certificate for development
- CORS is configured for the Vercel frontend
- The backend runs on port 8080 with HTTPS
- Database data is persisted using Kubernetes PersistentVolumes
