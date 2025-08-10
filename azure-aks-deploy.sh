#!/bin/bash

# Azure AKS Deployment Script for TimeSheet Management System
# This script creates an AKS cluster and deploys the application

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
RESOURCE_GROUP="timesheet-rg"
LOCATION="centralus"
CLUSTER_NAME="timesheet-aks"
NODE_COUNT=1
NODE_SIZE="Standard_B2s"
NAMESPACE="timesheet-management"
IMAGE_NAME="timesheet-management"
IMAGE_TAG="latest"
ACR_NAME="timesheetacr"

# Function to check Azure CLI
check_azure_cli() {
    if ! command -v az &> /dev/null; then
        print_error "Azure CLI is not installed. Please install it first."
        print_status "Installation guide: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
        exit 1
    fi
    
    if ! az account show &> /dev/null; then
        print_error "Azure CLI is not logged in. Please run 'az login' first."
        exit 1
    fi
    
    print_success "Azure CLI is ready"
}

# Function to check kubectl
check_kubectl() {
    if ! command -v kubectl &> /dev/null; then
        print_error "kubectl is not installed. Please install it first."
        print_status "Installation guide: https://kubernetes.io/docs/tasks/tools/"
        exit 1
    fi
    
    print_success "kubectl is ready"
}

# Function to create resource group
create_resource_group() {
    print_status "Creating resource group..."
    
    if az group show --name $RESOURCE_GROUP &> /dev/null; then
        print_warning "Resource group $RESOURCE_GROUP already exists"
    else
        az group create --name $RESOURCE_GROUP --location $LOCATION
        print_success "Resource group $RESOURCE_GROUP created"
    fi
}

# Function to create Azure Container Registry
create_acr() {
    print_status "Creating Azure Container Registry..."
    
    if az acr show --name $ACR_NAME --resource-group $RESOURCE_GROUP &> /dev/null; then
        print_warning "ACR $ACR_NAME already exists"
    else
        az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic --location $LOCATION
        print_success "ACR $ACR_NAME created"
    fi
    
    # Get ACR login server
    ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --resource-group $RESOURCE_GROUP --query loginServer --output tsv)
    echo "ACR_LOGIN_SERVER=$ACR_LOGIN_SERVER" > azure-config.txt
}

# Function to create AKS cluster
create_aks_cluster() {
    print_status "Creating AKS cluster..."
    
    if az aks show --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME &> /dev/null; then
        print_warning "AKS cluster $CLUSTER_NAME already exists"
    else
        az aks create \
            --resource-group $RESOURCE_GROUP \
            --name $CLUSTER_NAME \
            --node-count $NODE_COUNT \
            --node-vm-size $NODE_SIZE \
            --enable-addons monitoring \
            --generate-ssh-keys \
            --attach-acr $ACR_NAME
        
        print_success "AKS cluster $CLUSTER_NAME created"
    fi
    
    # Get cluster credentials
    az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME --overwrite-existing
    print_success "Cluster credentials configured"
}

# Function to build and push Docker image
build_and_push_image() {
    print_status "Building and pushing Docker image..."
    
    source azure-config.txt
    
    # Build the image
    docker build -t $IMAGE_NAME:$IMAGE_TAG .
    
    # Tag for ACR
    docker tag $IMAGE_NAME:$IMAGE_TAG $ACR_LOGIN_SERVER/$IMAGE_NAME:$IMAGE_TAG
    
    # Login to ACR
    az acr login --name $ACR_NAME
    
    # Push to ACR
    docker push $ACR_LOGIN_SERVER/$IMAGE_NAME:$IMAGE_TAG
    
    print_success "Image pushed to ACR"
}

# Function to install NGINX Ingress Controller
install_ingress_controller() {
    print_status "Installing NGINX Ingress Controller..."
    
    # Add the NGINX Ingress Controller Helm repository
    helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
    helm repo update
    
    # Install NGINX Ingress Controller
    helm install nginx-ingress ingress-nginx/ingress-nginx \
        --namespace ingress-nginx \
        --create-namespace \
        --set controller.replicaCount=1 \
        --set controller.nodeSelector."kubernetes\.io/os"=linux \
        --set defaultBackend.nodeSelector."kubernetes\.io/os"=linux
    
    print_success "NGINX Ingress Controller installed"
}

# Function to update Kubernetes manifests for Azure
update_manifests_for_azure() {
    print_status "Updating Kubernetes manifests for Azure..."
    
    source azure-config.txt
    
    # Update app deployment to use ACR image
    sed -i.bak "s|image: timesheet-management:latest|image: $ACR_LOGIN_SERVER/$IMAGE_NAME:$IMAGE_TAG|g" k8s/app-deployment.yaml
    
    # Update image pull policy
    sed -i.bak "s|imagePullPolicy: IfNotPresent|imagePullPolicy: Always|g" k8s/app-deployment.yaml
    
    # Add image pull secrets if needed
    # kubectl create secret docker-registry acr-secret \
    #     --docker-server=$ACR_LOGIN_SERVER \
    #     --docker-username=$(az acr credential show --name $ACR_NAME --query username --output tsv) \
    #     --docker-password=$(az acr credential show --name $ACR_NAME --query passwords[0].value --output tsv) \
    #     --namespace=$NAMESPACE
    
    print_success "Kubernetes manifests updated for Azure"
}

# Function to deploy to AKS
deploy_to_aks() {
    print_status "Deploying to AKS..."
    
    # Create namespace
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    
    # Apply all resources
    kubectl apply -k k8s/
    
    print_success "Application deployed to AKS"
}

# Function to wait for deployment
wait_for_deployment() {
    print_status "Waiting for deployment to be ready..."
    
    # Wait for MySQL deployment
    kubectl wait --for=condition=available --timeout=300s deployment/timesheet-mysql -n $NAMESPACE
    
    # Wait for application deployment
    kubectl wait --for=condition=available --timeout=300s deployment/timesheet-app -n $NAMESPACE
    
    print_success "All deployments are ready"
}

# Function to get public IP
get_public_ip() {
    print_status "Getting public IP address..."
    
    # Wait for ingress controller to get public IP
    sleep 30
    
    PUBLIC_IP=$(kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
    
    if [ ! -z "$PUBLIC_IP" ]; then
        echo "PUBLIC_IP=$PUBLIC_IP" >> azure-config.txt
        print_success "Public IP: $PUBLIC_IP"
    else
        print_warning "Public IP not available yet. Please check later with:"
        print_status "kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx"
    fi
}

# Function to show deployment status
show_status() {
    print_status "Deployment status:"
    
    echo ""
    echo "=== AKS Cluster ==="
    az aks show --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME --query "{name:name,location:location,resourceGroup:resourceGroup,nodeCount:agentPoolProfiles[0].count,nodeSize:agentPoolProfiles[0].vmSize}" -o table
    
    echo ""
    echo "=== Pods ==="
    kubectl get pods -n $NAMESPACE
    
    echo ""
    echo "=== Services ==="
    kubectl get services -n $NAMESPACE
    
    echo ""
    echo "=== Ingress ==="
    kubectl get ingress -n $NAMESPACE
    
    echo ""
    echo "=== Public IP ==="
    kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx
}

# Function to get access information
get_access_info() {
    print_status "Access information:"
    
    source azure-config.txt
    
    echo ""
    echo "Azure Resources:"
    echo "  - Resource Group: $RESOURCE_GROUP"
    echo "  - AKS Cluster: $CLUSTER_NAME"
    echo "  - ACR: $ACR_NAME"
    echo "  - ACR Login Server: $ACR_LOGIN_SERVER"
    
    echo ""
    echo "Application Access:"
    if [ ! -z "$PUBLIC_IP" ]; then
        echo "  - Public IP: $PUBLIC_IP"
        echo "  - Application URL: http://$PUBLIC_IP"
    else
        echo "  - Public IP: Not available yet"
        echo "  - Check with: kubectl get service nginx-ingress-ingress-nginx-controller -n ingress-nginx"
    fi
    
    echo ""
    echo "Default users:"
    echo "  - admin/password123"
    echo "  - manager/password123"
    echo "  - employee1/password123"
    echo "  - employee2/password123"
    
    echo ""
    echo "Useful commands:"
    echo "  - View logs: kubectl logs -f deployment/timesheet-app -n $NAMESPACE"
    echo "  - Scale app: kubectl scale deployment timesheet-app -n $NAMESPACE --replicas=5"
    echo "  - Get cluster credentials: az aks get-credentials --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME"
}

# Function to clean up resources
cleanup() {
    print_status "Cleaning up Azure resources..."
    
    read -p "Are you sure you want to delete all resources? (y/N): " CONFIRM
    
    if [[ $CONFIRM =~ ^[Yy]$ ]]; then
        # Delete AKS cluster
        az aks delete --resource-group $RESOURCE_GROUP --name $CLUSTER_NAME --yes
        
        # Delete ACR
        az acr delete --resource-group $RESOURCE_GROUP --name $ACR_NAME --yes
        
        # Delete resource group
        az group delete --name $RESOURCE_GROUP --yes
        
        # Remove config file
        rm -f azure-config.txt
        
        print_success "All resources cleaned up"
    else
        print_status "Cleanup cancelled"
    fi
}

# Main deployment function
deploy_to_azure_aks() {
    print_status "Starting Azure AKS deployment..."
    
    check_azure_cli
    check_kubectl
    
    create_resource_group
    create_acr
    create_aks_cluster
    build_and_push_image
    install_ingress_controller
    update_manifests_for_azure
    deploy_to_aks
    wait_for_deployment
    get_public_ip
    
    print_success "Azure AKS deployment completed successfully!"
    show_status
    get_access_info
}

# Main script
main() {
    echo "=========================================="
    echo "Azure AKS Deployment for TimeSheet Management"
    echo "=========================================="
    echo ""
    echo "Options:"
    echo "1. Deploy to Azure AKS (Full deployment)"
    echo "2. Show deployment status"
    echo "3. Get access information"
    echo "4. Clean up resources"
    echo "5. Exit"
    echo ""
    
    read -p "Select option (1-5): " choice
    
    case $choice in
        1) deploy_to_azure_aks ;;
        2) show_status ;;
        3) get_access_info ;;
        4) cleanup ;;
        5) print_status "Goodbye!"; exit 0 ;;
        *) print_error "Invalid option"; main ;;
    esac
}

# Run main function
main
