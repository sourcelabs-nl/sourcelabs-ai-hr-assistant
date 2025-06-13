# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.0 application written in Kotlin, using Java 21. The project is called "sourcechat" and appears to be a chat application framework. It uses Maven as the build tool and includes Testcontainers for integration testing.

## Requirements

This Spring Boot application is an AI based chat application built with Spring AI.

- Containing an endpoint for sending chats to a Spring AI ChatClient. 
- Use a local LLM using Ollama llama3.2 for chat running on port 1234.
- Instructions for claude with Spring AI can be found: https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html
- It should store chat memory into a postgres database.
- It should use a postgres vector database for RAG.
- It should use the following system prompt "You are the Sourcelabs HR assistant. You provide information about leave hours, billable client hours and the employee manual."
- The LLM should connect to the hour registration system using MCP. This application will provide endpoints for this.
- Use Ollama embeddings model: 
  - Spring AI documentation for ollama embeddings: https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html
  - Add the ollama embeddings model to docker compose config.
- Add functionality to register leave hours and billable client hours:
  - the user will only use the LLM chat to specify the hours they want to register
  - the application should provide endpoints to register leave hours
  - the application should provide endpoints to register billable client hours
    - billable client hours should indicate the client name, location and hours worked including type of travel (if applicable) 
    - if traveled by car or bike also specify kilometers traveled (from/to) 
  - The application should expose this functionality via a REST API.
  - The application should expose this functionality using an MCP server.
  - MCP server instructions can be found here: 
    - https://piotrminkowski.com/2025/03/17/using-model-context-protocol-mcp-with-spring-ai/
    - https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html
  - Use Web
  - MCP example can be found here:https://www.baeldung.com/spring-ai-model-context-protocol-mcp
- The LLM should use an MCP client for leave hours and billable client hours.
  - MCP client instructions can be found here: https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html
- The application exposes a UI which is connected to the application that allows for chatting with the LLM
  - the user will only use the LLM chat to specify the hours they want to register
  - the UI should be build using React and TypeScript
  - the UI should be able to connect to the Spring AI ChatClient
  - the UI should use material UI for styling
- Use Spring Data JDB for database interactions.
- An example of a Spring AI application can be found here: https://piotrminkowski.com/2025/01/28/getting-started-with-spring-ai-and-chat-model/
- Use Spring Boot starters for Spring AI.
- Add a docker compose file for the postgres database and make sure to add the vector extension.

# Considerations

- Whenever in doubt, ask the user for input.
- Whenever a choice needs to be made, ask the user for input.
- When you complete a task 
  - check if code compiles 
  - check if application runs
  - create a commit with the message "feat: <description of the feature>".

## Key Technologies

- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.0 with Spring Web
- **Build Tool**: Maven
- **Java Version**: 21
- **Testing**: JUnit 5, Testcontainers, Spring Boot Test

## Development Commands

### Build and Run
```bash
# Run the application
./mvnw spring-boot:run

# Build the project
./mvnw clean compile

# Create executable JAR
./mvnw clean package
```

### Testing
```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=SourcechatApplicationTests

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Development Utilities
```bash
# Clean build artifacts
./mvnw clean

# Validate project structure
./mvnw validate

# Check for dependency updates
./mvnw versions:display-dependency-updates
```

## Architecture Notes

- **Package Structure**: All code is organized under `nl.sourcelabs.sourcechat`
- **Main Application**: `SourcechatApplication.kt` is the Spring Boot entry point
- **Test Configuration**: Uses `TestcontainersConfiguration` for integration testing setup
- **Test Runner**: `TestSourcechatApplication.kt` provides a test-specific application runner

## Important Dependencies

- **Spring AI BOM**: Version 1.0.0 is managed via dependencyManagement, suggesting AI/ML functionality will be added
- **Jackson Kotlin Module**: For JSON serialization/deserialization
- **Testcontainers**: For integration testing with real services

## Testing Strategy

The project uses a layered testing approach:
- Unit tests with JUnit 5 and Kotlin test support
- Integration tests with Spring Boot Test and Testcontainers
- Separate test application runner for development testing