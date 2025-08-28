#!/bin/bash

# Docker Build Script for RushHour Backend
# This script builds Docker images for different environments

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
ENVIRONMENT="local"
IMAGE_NAME="rh-backend"
TAG="latest"
PUSH=false

# Function to print usage
print_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --env ENV       Environment to build (default|prod) [default: default]"
    echo "  -n, --name NAME     Docker image name [default: rh-backend]"
    echo "  -t, --tag TAG       Docker image tag [default: latest]"
    echo "  -p, --push          Push image to registry after build"
    echo "  -h, --help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0                           # Build local environment"
    echo "  $0 -e prod                   # Build production environment"
    echo "  $0 -e prod -t v1.0.0        # Build production with specific tag"
    echo "  $0 -e prod -p                # Build and push production image"
}

# Function to print colored output
print_info() {
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

# Function to validate environment
validate_environment() {
    if [[ "$ENVIRONMENT" != "local" && "$ENVIRONMENT" != "prod" ]]; then
        print_error "Invalid environment: $ENVIRONMENT. Must be 'local' or 'prod'"
        exit 1
    fi
}

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    
    # Check if environment file exists
    if [[ ! -f "env.${ENVIRONMENT}" ]]; then
        print_error "Environment file 'env.${ENVIRONMENT}' not found!"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
}

# Function to build Docker image
build_image() {
    print_info "Building Docker image for environment: $ENVIRONMENT"
    print_info "Image: $IMAGE_NAME:$TAG"
    
    # Build the image
    docker build \
        --build-arg BUILD_ENV=$ENVIRONMENT \
        -t $IMAGE_NAME:$TAG \
        -t $IMAGE_NAME:${ENVIRONMENT}-${TAG} \
        .
    
    if [ $? -eq 0 ]; then
        print_success "Docker image built successfully!"
        print_info "Image tags:"
        print_info "  - $IMAGE_NAME:$TAG"
        print_info "  - $IMAGE_NAME:${ENVIRONMENT}-${TAG}"
    else
        print_error "Failed to build Docker image"
        exit 1
    fi
}

# Function to push Docker image
push_image() {
    if [ "$PUSH" = true ]; then
        print_info "Pushing Docker image to registry..."
        
        # Push both tags
        docker push $IMAGE_NAME:$TAG
        docker push $IMAGE_NAME:${ENVIRONMENT}-${TAG}
        
        if [ $? -eq 0 ]; then
            print_success "Docker image pushed successfully!"
        else
            print_error "Failed to push Docker image"
            exit 1
        fi
    fi
}

# Function to show build summary
show_summary() {
    echo ""
    print_success "Build completed successfully!"
    echo ""
    echo "Build Summary:"
    echo "  Environment: $ENVIRONMENT"
    echo "  Image Name: $IMAGE_NAME"
    echo "  Tags: $TAG, ${ENVIRONMENT}-${TAG}"
    echo "  Pushed: $PUSH"
    echo ""
    echo "To run the container:"
    echo "  docker run -p 8008:8008 $IMAGE_NAME:$TAG"
    echo ""
    echo "To run the container:"
echo "  docker run -p 8008:8008 --env-file env.${BUILD_ENV} rh-backend:${BUILD_ENV}"
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--env)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -n|--name)
            IMAGE_NAME="$2"
            shift 2
            ;;
        -t|--tag)
            TAG="$2"
            shift 2
            ;;
        -p|--push)
            PUSH=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            print_usage
            exit 1
            ;;
    esac
done

# Main execution
main() {
    print_info "Starting Docker build for RushHour Backend"
    print_info "Environment: $ENVIRONMENT"
    
    # Validate environment
    validate_environment
    
    # Check prerequisites
    check_prerequisites
    
    # Build image
    build_image
    
    # Push image if requested
    push_image
    
    # Show summary
    show_summary
}

# Run main function
main
