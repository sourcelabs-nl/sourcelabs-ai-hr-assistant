#!/bin/bash

# Setup script to initialize Ollama models required for the application
# This script pulls the required models for chat and embeddings

set -e

echo "Setting up Ollama models for Sourcechat application..."

# Check if Ollama is installed
if ! command -v ollama &> /dev/null; then
    echo "Error: Ollama is not installed. Please install Ollama first."
    echo "Visit: https://ollama.ai/download"
    exit 1
fi

# Pull llama3.2 model for chat (will be served on port 1234)
echo "Pulling llama3.2 model for chat functionality..."
ollama pull llama3.2

# Pull nomic-embed-text model for embeddings (will be served on port 11434)
echo "Pulling nomic-embed-text model for embeddings..."
ollama pull nomic-embed-text

echo "All required models have been pulled successfully!"
echo ""
echo "To start the application:"
echo "1. Start Docker containers: cd docker && docker-compose up -d"
echo "2. Start Ollama chat server: ollama serve --port 1234 llama3.2"
echo "3. Start Ollama embeddings server: ollama serve --port 11434 nomic-embed-text"
echo "4. Build and run the application: ./mvnw spring-boot:run"