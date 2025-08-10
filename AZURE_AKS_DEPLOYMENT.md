# Azure AKS Deployment Guide for TimeSheet Management System

This guide provides detailed instructions for deploying the TimeSheet Management System to Azure Kubernetes Service (AKS).

## 🏗️ Architecture Overview

```
Internet
    ↓
Azure Load Balancer
    ↓
NGINX Ingress Controller
    ↓
TimeSheet App Pods (2+ replicas)
    ↓
MySQL Pod (1 replica)
    ↓
Azure Managed Disk (10Gi)
```

## 📋 Prerequisites

### 1. Azure Account
- Active Azure subscription
- Sufficient permissions to create resources
- Billing enabled

### 2. Required Tools
- **Azure CLI**: For Azure resource management
- **kubectl**: Kubernetes command-line tool
- **Docker**: For building container images
- **Helm**: For installing ingress controller (optional)

### 3. Azure Resource Requirements
- **Resource Group**: For organizing resources
- **AKS Cluster**: 2 nodes minimum
- **Azure Container Registry**: For storing Docker images
- **Load Balancer**: For external access

## 🚀 Quick Start

### Option 1: Automated Deployment

```bash
# Make the deployment script executable
chmod +x azure-aks-deploy.sh

# Run the deployment script
./azure-aks-deploy.sh
# Select option 1: Deploy to Azure AKS (Full deployment)
```

### Option 2: Manual Deployment

Follow the step-by-step instructions below.

## 📝 Step-by-Step Manual Deployment

### Step 1: Install and Configure Azure CLI

```bash
# Install Azure CLI (if not already installed)
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login to Azure
az login

# Set subscription (if you have multiple)
az account set --subscription "your-subscription-id"

# Verify login
az account show
```

### Step 2: Create Resource Group

```bash
# Create resource group
az group create --name timesheet-rg --location eastus

# Verify resource group
az group show --name timesheet-rg
```

### Step 3: Create Azure Container Registry

```bash
# Create ACR
az acr create --resource-group timesheet-rg --name timesheetacr --sku Basic

# Get ACR login server
ACR_LOGIN_SERVER=$(az acr show --name timesheetacr --resource-group timesheet-rg --query loginServer --output tsv)
echo "ACR Login Server: $ACR_LOGIN_SERVER"

# Login to ACR
az acr login --name timesheetacr
```

### Step 4: Create AKS Cluster

```bash
# Create AKS cluster
az aks create \
    --resource-group timesheet-rg \
    --name timesheet-aks \
    --node-count 2 \
    --node-vm-size Standard_B2s \
    --enable-addons monitoring \
    --generate-ssh-keys \
    --attach-acr timesheetacr

# Get cluster credentials
az aks get-credentials --resource-group timesheet-rg --name timesheet-aks --overwrite-existing

# Verify cluster connection
kubectl cluster-info
```

### Step 5: Build and Push Docker Image

```bash
# Build the application image
docker build -t timesheet-management:latest .

# Tag for ACR
docker tag timesheet-management:latest $ACR_LOGIN_SERVER/timesheet-management:latest

# Push to ACR
docker push $ACR_LOGIN_SERVER/timesheet-management:latest
```

### Step 6: Install NGINX Ingress Controller

```bash
# Add Helm repository
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

# Install NGINX Ingress Controller
helm install nginx-ingress ingress-nginx/ingress-nginx \
    --namespace ingress-nginx \
    --create-namespace \
    --set controller.replicaCount=1 \
    --set controller.nodeSelector."kubernetes\.io/os"=linux \
    --set defaultBackend.nodeSelector."kubernetes\.io/os"=linux

# Wait for ingress controller to be ready
kubectl wait --namespace ingress-nginx \
    --for=condition=ready pod \
    --selector=app.kubernetes.io/component=controller \
    --timeout=120s
```

### Step 7: Update Kubernetes Manifests

```bash
# Update app deployment to use ACR image
sed -i "s|image: timesheet-management:latest|image: $ACR_LOGIN_SERVER/timesheet-management:latest|g" k8s/app-deployment.yaml

# Update image pull policy
sed -i "s|imagePullPolicy: IfNotPresent|imagePullPolicy: Always|g" k8s/app-deployment.yaml
```

### Step 8: Deploy Application

```bash
# Create namespace
kubectl create namespace timesheet-management

# Deploy all resources
kubectl apply -k k8s/

# Wait for deployments to be ready
kubectl wait --for=condition=available --timeout=300s deployment/timesheet-mysql -n timesheet-management
kubectl wait --for=condition=available --timeout=300s deployment/timesheet-app -n timesheet-management
```

### Step 9: Get Public IP

```bash
# Get public IP of ingress controller
kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx

# Or get the IP directly
PUBLIC_IP=$(kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "Public IP: $PUBLIC_IP"
```

## 🔧 Configuration

### Azure Resources Created

- **Resource Group**: `timesheet-rg`
- **AKS Cluster**: `timesheet-aks`
- **Container Registry**: `timesheetacr`
- **Load Balancer**: Auto-created by AKS

### Cluster Configuration

```bash
# Node count: 2
# Node size: Standard_B2s (2 vCPUs, 4 GB RAM)
# Monitoring: Enabled
# ACR integration: Enabled
```

### Application Configuration

```yaml
# Environment variables
SPRING_PROFILES_ACTIVE: prod
APP_JWT_SECRET: (from Kubernetes secret)
APP_JWT_EXPIRATION_IN_MS: 86400000
SERVER_PORT: 8080

# Resource limits
requests:
  memory: 512Mi
  cpu: 500m
limits:
  memory: 1Gi
  cpu: 1000m
```

## 🌐 Access and Networking

### Service Types

- **MySQL Service**: ClusterIP (internal access only)
- **Application Service**: ClusterIP (accessed via Ingress)
- **Ingress Controller**: LoadBalancer (external access)

### Public Access

```bash
# Get public IP
kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx

# Access application
curl http://<PUBLIC_IP>/api/health
```

### Custom Domain (Optional)

```bash
# Update ingress with custom domain
kubectl patch ingress timesheet-ingress -n timesheet-management -p '{"spec":{"rules":[{"host":"your-domain.com"}]}}'
```

## 📊 Monitoring and Management

### Azure Monitor

```bash
# Enable monitoring for AKS
az aks enable-addons --addons monitoring --resource-group timesheet-rg --name timesheet-aks

# View metrics in Azure portal
az aks browse --resource-group timesheet-rg --name timesheet-aks
```

### Kubernetes Monitoring

```bash
# View pod status
kubectl get pods -n timesheet-management

# View logs
kubectl logs -f deployment/timesheet-app -n timesheet-management

# Check resource usage
kubectl top pods -n timesheet-management

# Check HPA status
kubectl get hpa -n timesheet-management
```

### Azure Container Registry

```bash
# List images in ACR
az acr repository list --name timesheetacr

# View image tags
az acr repository show-tags --name timesheetacr --repository timesheet-management

# Delete old images
az acr repository delete --name timesheetacr --image timesheet-management:old-tag
```

## 🔄 Scaling and Updates

### Manual Scaling

```bash
# Scale application
kubectl scale deployment timesheet-app -n timesheet-management --replicas=5

# Scale AKS cluster
az aks scale --resource-group timesheet-rg --name timesheet-aks --node-count 3
```

### Auto Scaling

```bash
# Enable cluster autoscaler
az aks update \
    --resource-group timesheet-rg \
    --name timesheet-aks \
    --enable-cluster-autoscaler \
    --min-count 1 \
    --max-count 5

# Check autoscaler status
kubectl get nodes
kubectl describe node <node-name>
```

### Rolling Updates

```bash
# Update application image
kubectl set image deployment/timesheet-app timesheet-app=$ACR_LOGIN_SERVER/timesheet-management:v2.0.0 -n timesheet-management

# Check rollout status
kubectl rollout status deployment/timesheet-app -n timesheet-management

# Rollback if needed
kubectl rollout undo deployment/timesheet-app -n timesheet-management
```

## 💾 Backup and Recovery

### Database Backup

```bash
# Create database backup
kubectl exec deployment/timesheet-mysql -n timesheet-management -- \
    mysqldump -u root -pGlobe@1234 timesheetdb > backup.sql

# Restore database
kubectl exec -i deployment/timesheet-mysql -n timesheet-management -- \
    mysql -u root -pGlobe@1234 timesheetdb < backup.sql
```

### ACR Backup

```bash
# Export image from ACR
az acr repository show-manifests --name timesheetacr --repository timesheet-management

# Import image to ACR
az acr import --name timesheetacr --source docker.io/library/nginx:latest --image nginx:latest
```

### AKS Backup

```bash
# Export cluster configuration
az aks show --resource-group timesheet-rg --name timesheet-aks --output yaml > cluster-config.yaml

# Backup persistent volumes (requires manual process)
# Use Azure Backup or manual snapshot
```

## 🚨 Troubleshooting

### Common Issues

1. **ACR Authentication Issues**
   ```bash
   # Re-authenticate with ACR
   az acr login --name timesheetacr
   
   # Check ACR credentials
   az acr credential show --name timesheetacr
   ```

2. **AKS Connection Issues**
   ```bash
   # Refresh cluster credentials
   az aks get-credentials --resource-group timesheet-rg --name timesheet-aks --overwrite-existing
   
   # Check cluster status
   az aks show --resource-group timesheet-rg --name timesheet-aks
   ```

3. **Ingress Issues**
   ```bash
   # Check ingress controller
   kubectl get pods -n ingress-nginx
   
   # Check ingress status
   kubectl describe ingress timesheet-ingress -n timesheet-management
   
   # Check load balancer
   kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx
   ```

4. **Resource Issues**
   ```bash
   # Check node resources
   kubectl describe nodes
   
   # Check pod events
   kubectl describe pod <pod-name> -n timesheet-management
   
   # Check resource quotas
   kubectl get resourcequota -n timesheet-management
   ```

### Performance Issues

1. **High Resource Usage**
   ```bash
   # Check resource usage
   kubectl top pods -n timesheet-management
   kubectl top nodes
   
   # Scale up cluster
   az aks scale --resource-group timesheet-rg --name timesheet-aks --node-count 4
   ```

2. **Slow Database Queries**
   ```bash
   # Access MySQL and check slow queries
   kubectl exec -it deployment/timesheet-mysql -n timesheet-management -- mysql -u root -p
   ```

## 🔒 Security

### Network Security

```bash
# Enable network policies
az aks update \
    --resource-group timesheet-rg \
    --name timesheet-aks \
    --enable-network-policy

# Apply network policies
kubectl apply -f k8s/network-policy.yaml
```

### RBAC Configuration

```bash
# Create service account
kubectl create serviceaccount timesheet-sa -n timesheet-management

# Create role and role binding
kubectl apply -f k8s/rbac.yaml
```

### Azure Security Center

```bash
# Enable Azure Security Center for containers
az security auto-provisioning-setting update --auto-provision on

# Check security recommendations
az security recommendation list --resource-group timesheet-rg
```

## 💰 Cost Optimization

### Estimated Monthly Costs (East US)

- **AKS Cluster (2 nodes)**: ~$60/month
- **ACR (Basic)**: ~$5/month
- **Load Balancer**: ~$20/month
- **Monitoring**: ~$10/month
- **Total**: ~$95/month

### Cost Reduction Tips

1. **Use Spot Instances**
   ```bash
   az aks nodepool add \
       --resource-group timesheet-rg \
       --cluster-name timesheet-aks \
       --name spotpool \
       --priority Spot \
       --eviction-policy Delete \
       --spot-max-price -1
   ```

2. **Scale Down During Off-Hours**
   ```bash
   # Scale down to 1 node during off-hours
   az aks scale --resource-group timesheet-rg --name timesheet-aks --node-count 1
   ```

3. **Use Reserved Instances**
   ```bash
   # Purchase reserved instances for 1-3 year commitments
   # 40-60% cost savings
   ```

## 🧹 Cleanup

### Delete All Resources

```bash
# Delete AKS cluster
az aks delete --resource-group timesheet-rg --name timesheet-aks --yes

# Delete ACR
az acr delete --resource-group timesheet-rg --name timesheetacr --yes

# Delete resource group
az group delete --name timesheet-rg --yes
```

### Partial Cleanup

```bash
# Delete only the application
kubectl delete -k k8s/

# Delete namespace
kubectl delete namespace timesheet-management

# Keep AKS cluster for other applications
```

## 📞 Support

For Azure AKS deployment issues:
1. Check Azure portal for resource status
2. Review AKS logs in Azure Monitor
3. Check Kubernetes events and logs
4. Verify network connectivity
5. Check Azure service health

## 🔗 Useful Links

- [Azure AKS Documentation](https://docs.microsoft.com/en-us/azure/aks/)
- [Azure Container Registry](https://docs.microsoft.com/en-us/azure/container-registry/)
- [AKS Best Practices](https://docs.microsoft.com/en-us/azure/aks/best-practices)
- [Azure CLI Reference](https://docs.microsoft.com/en-us/cli/azure/)
- [Kubernetes on Azure](https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough)
