#!/bin/bash

echo "Starting Ollama server..."
ollama serve & sleep 5 & ollama pull nomic-embed-text