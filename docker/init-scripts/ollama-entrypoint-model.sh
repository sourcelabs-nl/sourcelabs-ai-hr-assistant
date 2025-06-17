#!/bin/bash

echo "Starting Ollama server..."
ollama serve & sleep 5 & ollama pull llama3.2 & ollama serve
