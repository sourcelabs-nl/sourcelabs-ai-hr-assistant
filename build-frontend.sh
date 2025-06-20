#!/bin/bash

# Build frontend script for Maven integration
# This script builds the React frontend and copies the built files to Spring Boot's static resources

set -e

echo "Building frontend..."

# Navigate to frontend directory
cd src/main/frontend

# Install dependencies if node_modules doesn't exist or package-lock.json is newer
if [ ! -d "node_modules" ] || [ "package-lock.json" -nt "node_modules" ]; then
    echo "Installing frontend dependencies..."
    npm install
fi

# Build the React application
echo "Building React application..."
npm run build

# Copy built files to Spring Boot static resources
echo "Copying built files to Spring Boot static resources..."
rm -rf ../resources/static/*
rm -rf ../resources/build/*
cp -r build/static/* ../resources/public/static/
cp build/index.html ../resources/public/
echo "Frontend build completed successfully!"