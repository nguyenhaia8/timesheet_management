# Kubernetes Deployment Guide for TimeSheet Management System

This guide provides detailed instructions for deploying the TimeSheet Management System to Kubernetes.

## 🏗️ Architecture Overview

```
Internet
    ↓
Ingress Controller (NGINX)
    ↓
TimeSheet App (2+ replicas)
    ↓
MySQL Database (1 replica)
    ↓
Persistent Volume (10Gi)
```

## 📋 Prerequisites

### 1. Kubernetes Cluster
- **Local**: Minikube, Docker Desktop, or Kind
- **Cloud**: EKS, GKE, AKS, or any managed Kubernetes service
- **Self-hosted**: On-premises Kubernetes cluster

### 2. Required Tools
- **kubectl**: Kubernetes command-line tool
- **Docker**: For building container images
- **kustomize**: For managing Kubernetes resources (optional)

### 3. Cluster Requirements
- **CPU**: Minimum 2 cores
- **Memory**: Minimum 4GB RAM
- **Storage**: 10GB for persistent volume
- **Ingress Controller**: NGINX Ingress Controller

## 🚀 Quick Start

### Option 1: Automated Deployment

```bash
# Make the deployment script executable
chmod +x k8s-deploy.sh

# Run the deployment script
./k8s-deploy.sh
# Select option 1: Deploy to Kubernetes (Full deployment)
```

### Option 2: Manual Deployment

Follow the step-by-step instructions below.

## 📝 Step-by-Step Manual Deployment

### Step 1: Build Docker Image

```bash
# Build the application image
docker build -t timesheet-management:latest .

# For production, tag and push to registry
docker tag timesheet-management:latest your-registry/timesheet-management:latest
docker push your-registry/timesheet-management:latest
```

### Step 2: Create Namespace

```bash
# Create namespace
kubectl create namespace timesheet-management

# Verify namespace
kubectl get namespace timesheet-management
```

### Step 3: Create Persistent Volume Directory

```bash
# Create directory for persistent volume
sudo mkdir -p /mnt/data/timesheet-mysql
sudo chmod 777 /mnt/data/timesheet-mysql
```

### Step 4: Deploy Resources

```bash
# Deploy all resources using kustomize
kubectl apply -k k8s/

# Or deploy individually
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/mysql-persistent-volume.yaml
kubectl apply -f k8s/mysql-init-configmap.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

### Step 5: Verify Deployment

```bash
# Check all resources
kubectl get all -n timesheet-management

# Check pods status
kubectl get pods -n timesheet-management

# Check services
kubectl get services -n timesheet-management

# Check persistent volumes
kubectl get pv,pvc -n timesheet-management
```

## 🔧 Configuration

### Environment Variables

The application uses the following environment variables:

```yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
- name: APP_JWT_SECRET
  valueFrom:
    secretKeyRef:
      name: timesheet-secrets
      key: jwt-secret
- name: APP_JWT_EXPIRATION_IN_MS
  value: "86400000"
- name: SERVER_PORT
  value: "8080"
```

### Secrets Management

Sensitive data is stored in Kubernetes secrets:

```bash
# View secrets
kubectl get secrets -n timesheet-management

# Update JWT secret
kubectl patch secret timesheet-secrets -n timesheet-management \
  -p='{"data":{"jwt-secret":"'$(echo -n "new-secret" | base64)'"}}'
```

### ConfigMap

Application configuration is stored in ConfigMap:

```bash
# View config
kubectl get configmap timesheet-config -n timesheet-management -o yaml

# Update config
kubectl edit configmap timesheet-config -n timesheet-management
```

## 📊 Resource Management

### Resource Limits

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "500m"
  limits:
    memory: "1Gi"
    cpu: "1000m"
```

### Horizontal Pod Autoscaler

The HPA automatically scales the application based on CPU and memory usage:

```yaml
minReplicas: 2
maxReplicas: 10
targetCPUUtilizationPercentage: 70
targetMemoryUtilizationPercentage: 80
```

### Persistent Storage

MySQL data is stored in a persistent volume:

```yaml
capacity:
  storage: 10Gi
accessModes:
  - ReadWriteOnce
```

## 🌐 Access and Networking

### Service Types

- **MySQL Service**: ClusterIP (internal access only)
- **Application Service**: ClusterIP (accessed via Ingress)

### Ingress Configuration

```yaml
host: timesheet.local
annotations:
  nginx.ingress.kubernetes.io/rewrite-target: /
  nginx.ingress.kubernetes.io/cors-allow-origin: "*"
```

### Port Forwarding (Local Access)

```bash
# Port forward for local access
kubectl port-forward service/timesheet-app-service 8080:80 -n timesheet-management

# Access at http://localhost:8080
```

## 🔍 Monitoring and Logging

### View Logs

```bash
# View application logs
kubectl logs -f deployment/timesheet-app -n timesheet-management

# View MySQL logs
kubectl logs -f deployment/timesheet-mysql -n timesheet-management

# View specific pod logs
kubectl logs -f <pod-name> -n timesheet-management
```

### Health Checks

The application includes health checks:

```yaml
livenessProbe:
  httpGet:
    path: /api/health
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 30

readinessProbe:
  httpGet:
    path: /api/health
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

### Metrics and Monitoring

```bash
# Check resource usage
kubectl top pods -n timesheet-management

# Check HPA status
kubectl get hpa -n timesheet-management

# Describe HPA
kubectl describe hpa timesheet-hpa -n timesheet-management
```

## 🔄 Scaling and Updates

### Manual Scaling

```bash
# Scale application
kubectl scale deployment timesheet-app -n timesheet-management --replicas=5

# Scale MySQL (not recommended for production)
kubectl scale deployment timesheet-mysql -n timesheet-management --replicas=1
```

### Rolling Updates

```bash
# Update image
kubectl set image deployment/timesheet-app timesheet-app=timesheet-management:v2.0.0 -n timesheet-management

# Check rollout status
kubectl rollout status deployment/timesheet-app -n timesheet-management

# Rollback if needed
kubectl rollout undo deployment/timesheet-app -n timesheet-management
```

## 💾 Backup and Recovery

### Database Backup

```bash
# Create backup
kubectl exec deployment/timesheet-mysql -n timesheet-management -- \
  mysqldump -u root -pGlobe@1234 timesheetdb > backup.sql

# Restore backup
kubectl exec -i deployment/timesheet-mysql -n timesheet-management -- \
  mysql -u root -pGlobe@1234 timesheetdb < backup.sql
```

### Persistent Volume Backup

```bash
# Backup persistent volume data
sudo tar -czf mysql-backup.tar.gz /mnt/data/timesheet-mysql/

# Restore persistent volume data
sudo tar -xzf mysql-backup.tar.gz -C /
```

## 🚨 Troubleshooting

### Common Issues

1. **Pods Not Starting**
   ```bash
   # Check pod events
   kubectl describe pod <pod-name> -n timesheet-management
   
   # Check pod logs
   kubectl logs <pod-name> -n timesheet-management
   ```

2. **Database Connection Issues**
   ```bash
   # Check MySQL pod status
   kubectl get pods -l app=timesheet-mysql -n timesheet-management
   
   # Check MySQL logs
   kubectl logs deployment/timesheet-mysql -n timesheet-management
   ```

3. **Persistent Volume Issues**
   ```bash
   # Check PV/PVC status
   kubectl get pv,pvc -n timesheet-management
   
   # Check PV details
   kubectl describe pv timesheet-mysql-pv
   ```

4. **Ingress Issues**
   ```bash
   # Check ingress status
   kubectl get ingress -n timesheet-management
   
   # Check ingress controller
   kubectl get pods -n ingress-nginx
   ```

### Performance Issues

1. **High Resource Usage**
   ```bash
   # Check resource usage
   kubectl top pods -n timesheet-management
   
   # Check HPA status
   kubectl get hpa -n timesheet-management
   ```

2. **Slow Database Queries**
   ```bash
   # Access MySQL and check slow queries
   kubectl exec -it deployment/timesheet-mysql -n timesheet-management -- mysql -u root -p
   ```

## 🔒 Security

### Network Policies

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: timesheet-network-policy
  namespace: timesheet-management
spec:
  podSelector:
    matchLabels:
      app: timesheet-app
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: ingress-nginx
    ports:
    - protocol: TCP
      port: 8080
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: timesheet-mysql
    ports:
    - protocol: TCP
      port: 3306
```

### RBAC Configuration

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: ServiceAccount
metadata:
  name: timesheet-sa
  namespace: timesheet-management
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: timesheet-role
  namespace: timesheet-management
rules:
- apiGroups: [""]
  resources: ["pods", "services"]
  verbs: ["get", "list", "watch"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: timesheet-role-binding
  namespace: timesheet-management
subjects:
- kind: ServiceAccount
  name: timesheet-sa
  namespace: timesheet-management
roleRef:
  kind: Role
  name: timesheet-role
  apiGroup: rbac.authorization.k8s.io
```

## 🧹 Cleanup

### Delete Deployment

```bash
# Delete all resources
kubectl delete -k k8s/

# Or delete individually
kubectl delete namespace timesheet-management
```

### Cleanup Persistent Volume

```bash
# Remove persistent volume data
sudo rm -rf /mnt/data/timesheet-mysql/
```

## 📞 Support

For Kubernetes deployment issues:
1. Check pod status: `kubectl get pods -n timesheet-management`
2. View logs: `kubectl logs <pod-name> -n timesheet-management`
3. Check events: `kubectl get events -n timesheet-management`
4. Verify network connectivity
5. Check resource limits and requests

## 🔗 Useful Links

- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [NGINX Ingress Controller](https://kubernetes.github.io/ingress-nginx/)
- [Kustomize Documentation](https://kustomize.io/)
- [Kubernetes Best Practices](https://kubernetes.io/docs/concepts/configuration/overview/)
