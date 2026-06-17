#!/bin/bash

# Script to build Docker images for Locki project

set -e

echo "=========================================="
echo "Locki Docker Build Script"
echo "=========================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default values
BUILD_ADMIN=true
BUILD_RESERVATIONS=true
PUSH_IMAGES=false
IMAGE_TAG="latest"
REGISTRY=""

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --admin-only)
      BUILD_RESERVATIONS=false
      shift
      ;;
    --reservations-only)
      BUILD_ADMIN=false
      shift
      ;;
    --push)
      PUSH_IMAGES=true
      shift
      ;;
    --tag)
      IMAGE_TAG="$2"
      shift 2
      ;;
    --registry)
      REGISTRY="$2"
      shift 2
      ;;
    --help)
      echo "Usage: $0 [OPTIONS]"
      echo ""
      echo "Options:"
      echo "  --admin-only          Build only manage-administration service"
      echo "  --reservations-only   Build only manage-reservations service"
      echo "  --push                Push images to registry"
      echo "  --tag TAG             Set image tag (default: latest)"
      echo "  --registry REGISTRY   Set registry URL (e.g., docker.io/myuser)"
      echo "  --help                Show this help message"
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      exit 1
      ;;
  esac
done

# Build manage-administration
if [ "$BUILD_ADMIN" = true ]; then
  echo -e "${YELLOW}Building manage-administration image...${NC}"
  docker build \
    -f manage-administration/Dockerfile \
    -t locki-admin:${IMAGE_TAG} \
    .

  if [ ! -z "$REGISTRY" ]; then
    docker tag locki-admin:${IMAGE_TAG} ${REGISTRY}/locki-admin:${IMAGE_TAG}
    if [ "$PUSH_IMAGES" = true ]; then
      echo -e "${YELLOW}Pushing locki-admin:${IMAGE_TAG}...${NC}"
      docker push ${REGISTRY}/locki-admin:${IMAGE_TAG}
    fi
  fi
  echo -e "${GREEN}✓ manage-administration built successfully${NC}"
  echo ""
fi

# Build manage-reservations
if [ "$BUILD_RESERVATIONS" = true ]; then
  echo -e "${YELLOW}Building manage-reservations image...${NC}"
  docker build \
    -f manage-reservations/Dockerfile \
    -t locki-reservations:${IMAGE_TAG} \
    .

  if [ ! -z "$REGISTRY" ]; then
    docker tag locki-reservations:${IMAGE_TAG} ${REGISTRY}/locki-reservations:${IMAGE_TAG}
    if [ "$PUSH_IMAGES" = true ]; then
      echo -e "${YELLOW}Pushing locki-reservations:${IMAGE_TAG}...${NC}"
      docker push ${REGISTRY}/locki-reservations:${IMAGE_TAG}
    fi
  fi
  echo -e "${GREEN}✓ manage-reservations built successfully${NC}"
  echo ""
fi

echo -e "${GREEN}=========================================="
echo "Build completed successfully!"
echo "==========================================${NC}"
echo ""

if [ "$BUILD_ADMIN" = true ]; then
  echo -e "${GREEN}locki-admin:${IMAGE_TAG}${NC}"
fi

if [ "$BUILD_RESERVATIONS" = true ]; then
  echo -e "${GREEN}locki-reservations:${IMAGE_TAG}${NC}"
fi

echo ""
echo -e "Run services with: ${YELLOW}docker-compose -f docker-compose-full.yaml up${NC}"

