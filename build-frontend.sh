#!/bin/bash

# Build the React frontend
echo "Building React frontend..."
cd frontend
npm install
npm run build

# Copy build files to Spring Boot static resources
echo "Copying build files to Spring Boot..."
cd ..
rm -rf src/main/resources/static/*
rm -rf src/main/resources/public/*

# Copy the build output
cp -r frontend/build/* src/main/resources/static/
cp frontend/build/index.html src/main/resources/public/

echo "Frontend build complete!"