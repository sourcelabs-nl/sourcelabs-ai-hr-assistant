# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SourceChat is a fully-functional Spring Boot 3.5.0 HR assistant application built with Kotlin and Java 21. It provides AI-powered chat functionality for hour registration and employee manual queries using local Ollama models and RAG capabilities.

## Current Implementation Status

✅ **COMPLETED FEATURES:**
- **AI Chat System**: Ollama llama3.2 for chat, nomic-embed-text for embeddings
- **Hour Registration**: Leave hours and billable client hours with full CRUD operations and validation
- **MCP Integration**: Server/client architecture for tool callbacks with structured error handling
- **RAG System**: PostgreSQL vector store with employee manual content and error recovery
- **Chat Memory**: MessageChatMemoryAdvisor with configurable message window for conversation continuity
- **Input Validation**: Jakarta Bean Validation with comprehensive constraints and business logic validation
- **Exception Handling**: Centralized GlobalExceptionHandler with RFC 7807 ProblemDetail responses and specific exception types
- **Security & Configuration**: CORS configuration, input sanitization, externalized settings with environment support
- **Comprehensive Logging**: Structured logging with consistent patterns and companion objects throughout application
- **Code Quality**: Kotlin idiomatic patterns, transactional service layer, extension functions for clean conversions
- **React Frontend**: Material UI chat interface with TypeScript
- **Database Schema**: PostgreSQL with pgvector extension and proper transaction boundaries
- **Docker Setup**: Multi-service composition with health checks

## Architecture

**Backend Stack:**
- Spring Boot 3.5.0 with Spring AI
- Kotlin 1.9.25, Java 21
- PostgreSQL with pgvector for vector storage
- Spring Data JDBC for database operations
- MCP (Model Context Protocol) for tool integration
- Log4j2 for structured logging and observability
- ProblemDetail (RFC 7807) for standardized error responses

**AI Stack:**
- **Chat Model**: Ollama llama3.2 (localhost:1234)
- **Embeddings**: Ollama nomic-embed-text (localhost:11434)
- **Vector Store**: PostgreSQL pgvector with 768-dimensional embeddings
- **RAG**: Employee manual content for context
- **Memory**: MessageWindowChatMemory with 20-message sliding window

**Frontend Stack:**
- React 18.2.0 with TypeScript 4.9.4
- Material UI 5.14.20 for styling
- Chat interface with real-time messaging
- Integrated with Spring Boot static resources

## Current Configuration

**Services (Docker Compose):**
```yaml
- postgres (5432): PostgreSQL with pgvector extension
- ollama-chat (1234): Ollama llama3.2 for chat
- ollama-embeddings (11434): Ollama nomic-embed-text for embeddings
```

**Key Endpoints:**
- `POST /api/chat`: Chat with HR assistant
- `POST /api/hours/leave`: Register leave hours
- `POST /api/hours/billable`: Register billable hours
- `GET /api/hours/leave/{employeeId}`: Get leave history
- `GET /api/hours/billable/{employeeId}`: Get billable history

**Database Tables:**
- `chat_messages`: Chat conversation history
- `leave_hours`: Leave hour registrations
- `billable_client_hours`: Billable hour registrations  
- `vector_store`: RAG document embeddings (768-dim)

## Setup Instructions

**1. Start Services:**
```bash
docker-compose up -d
```

**2. Pull AI Models:**
```bash
# Chat model
docker exec sourcechat-ollama-chat ollama pull llama3.2

# Embedding model  
docker exec sourcechat-ollama-embeddings ollama pull nomic-embed-text
```

**3. Build Frontend:**
```bash
./build-frontend.sh
```

**4. Run Application:**
```bash
./mvnw spring-boot:run
```

**5. Access Application:**
- Frontend: http://localhost:8080
- API: http://localhost:8080/api/*

## Features Implemented

**HR Assistant Chat:**
- Natural language hour registration
- Employee manual queries with RAG
- Tool-based hour registration and retrieval
- Session-based chat memory with automatic conversation continuity

**Hour Registration System:**
- Leave hours: Types, dates, approval workflow
- Billable hours: Client, location, travel tracking
- MCP tool integration for chat-based registration
- REST API for direct access

**RAG (Retrieval Augmented Generation):**
- Employee manual content in vector store
- Semantic search for relevant information
- Context-aware responses

**Frontend Interface:**
- Material UI chat interface
- Real-time messaging
- Example queries and suggestions
- Error handling and loading states

## Implementation Details

**Key Source Files:**
- `src/main/kotlin/nl/sourcelabs/sourcechat/config/ChatConfig.kt`: Chat client configuration with externalized settings and system prompt loading
- `src/main/kotlin/nl/sourcelabs/sourcechat/service/ChatService.kt`: Chat service with structured validation, specific exceptions, and modular design
- `src/main/kotlin/nl/sourcelabs/sourcechat/service/HourRegistrationService.kt`: Transactional hour registration with business validation and extension functions
- `src/main/kotlin/nl/sourcelabs/sourcechat/service/DocumentService.kt`: Vector store operations with comprehensive error handling
- `src/main/kotlin/nl/sourcelabs/sourcechat/mcp/HourRegistrationMcpService.kt`: MCP tools with input validation and structured error responses
- `src/main/kotlin/nl/sourcelabs/sourcechat/mcp/McpConfiguration.kt`: Separated MCP configuration beans
- `src/main/kotlin/nl/sourcelabs/sourcechat/controller/ChatController.kt`: REST API with comprehensive input validation and security
- `src/main/kotlin/nl/sourcelabs/sourcechat/controller/HourRegistrationController.kt`: Hour registration API with path validation and CORS configuration
- `src/main/kotlin/nl/sourcelabs/sourcechat/dto/ChatRequest.kt`: Request DTOs with Jakarta Bean Validation annotations
- `src/main/kotlin/nl/sourcelabs/sourcechat/dto/HourRegistrationDtos.kt`: Hour registration DTOs with comprehensive validation constraints
- `src/main/kotlin/nl/sourcelabs/sourcechat/exception/GlobalExceptionHandler.kt`: Enhanced exception handling with validation error support
- `src/main/kotlin/nl/sourcelabs/sourcechat/exception/ServiceExceptions.kt`: Specific exception hierarchy for different service domains
- `src/main/resources/system-prompt.txt`: Externalized system prompt for maintainability
- `src/main/resources/schema.sql`: Database schema with vector_store table
- `frontend/src/components/ChatInterface.tsx`: React chat UI component

**Current System Prompt:**
"You are the Sourcelabs HR assistant. You provide information about leave hours, billable client hours and the employee manual."

**Available MCP Tools:**
- `registerLeaveHours`: Register employee leave hours
- `registerBillableHours`: Register billable client hours
- `getLeaveHoursSummary`: Get total leave hours for year
- `getBillableHoursSummary`: Get total billable hours for year
- `getLeaveHistory`: Get recent leave history
- `getBillableHistory`: Get recent billable hours history

**Example Queries:**
- "Register 8 hours of sick leave for employee123 from 2025-06-13 to 2025-06-13"
- "Log 6 billable hours for ClientABC at Amsterdam office on 2025-06-13"
- "Show me my leave history for employee123"
- "How many billable hours did employee123 log this year?"

## Logging and Observability

**Comprehensive Logging Coverage:**
- **Chat Processing**: Session tracking, RAG search results, AI model calls, response metrics
- **MCP Tool Execution**: All 6 tools with input parameters, success/failure tracking, execution context
- **REST API Operations**: Request/response logging with key parameters (sessionId, employeeId, operation type)
- **Vector Store Operations**: Document searches, embedding operations, result counts
- **Database Operations**: Message persistence, hour registration, query execution

**Log Levels and Context:**
- **INFO**: Normal operation flow, key business events, performance metrics
- **WARN**: Recoverable issues, fallback usage, null responses  
- **ERROR**: Exceptions with full stack traces, operation failures, system errors

**Structured Logging Format:**
- Consistent parameter logging with `{}` placeholders
- Operation context (sessionId, employeeId, client names)
- Timing and volume metrics (response length, document counts)
- Clear component prefixes (`REST API:`, `MCP Tool:`, `RAG search:`)

**Exception Handling:**
- **GlobalExceptionHandler**: Centralized error handling with `@ControllerAdvice`
- **ProblemDetail Responses**: RFC 7807 compliant error format with structured details
- **Error Context**: Timestamp, path, status code, and descriptive messages
- **Exception Mapping**: Specific handlers for IllegalArgumentException, NoSuchElementException, RuntimeException

# Code Quality Standards

The codebase follows these quality standards implemented throughout the application:

## Input Validation
- All DTOs use Jakarta Bean Validation annotations (`@NotBlank`, `@Size`, `@Pattern`, etc.)
- Controllers use `@Valid` annotations for request validation
- Path variables are validated with regex patterns for security
- Business logic validation is implemented in service layers

## Exception Handling
- Specific exception classes for different domains (`ChatServiceException`, `HourRegistrationException`, etc.)
- `GlobalExceptionHandler` provides RFC 7807 compliant error responses
- Structured error logging with appropriate log levels
- Validation errors include field-specific error messages

## Configuration Management
- Application settings externalized to `application.properties`
- Environment-specific configuration support
- Configurable CORS origins, chat memory settings, and system prompts
- Proper resource loading with fallback mechanisms

## Security Features
- CORS configuration with environment-specific allowed origins
- Input sanitization and validation at multiple layers
- Path variable validation to prevent injection attacks
- Proper session ID format validation

## Service Layer Standards
- `@Transactional` annotations for proper transaction boundaries
- Companion objects for consistent logger initialization
- Extension functions for clean entity conversions
- Comprehensive business validation beyond input constraints

## Logging Standards
- Structured logging with consistent patterns across all classes
- Appropriate log levels (INFO for normal flow, WARN for recoverable issues, ERROR for exceptions)
- Contextual information in log messages (sessionId, employeeId, etc.)
- Companion objects for logger initialization: `private val logger = LogManager.getLogger(ClassName::class.java)`

# Considerations

- Whenever in doubt, ask the user for input.
- Whenever a choice needs to be made, ask the user for input.
- When you complete a task 
  - check if code compiles 
  - check if application runs
  - ensure all validation annotations are properly applied
  - verify error handling follows established patterns
  - confirm logging follows consistent standards
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

**Spring AI Stack:**
- **spring-ai-starter-model-ollama**: Ollama integration for chat and embeddings
- **spring-ai-starter-vector-store-pgvector**: PostgreSQL vector store
- **spring-ai-starter-mcp-client**: MCP client for tool integration  
- **spring-ai-starter-mcp-server-webmvc**: MCP server for exposing tools

**Database & Storage:**
- **postgresql**: PostgreSQL JDBC driver
- **spring-boot-starter-data-jdbc**: Spring Data JDBC
- **Jackson Kotlin Module**: JSON serialization/deserialization

**Testing:**
- **Testcontainers**: Integration testing with real services
- **JUnit 5**: Unit testing framework
- **Spring Boot Test**: Integration testing

## Testing Strategy

The project uses a layered testing approach:
- Unit tests with JUnit 5 and Kotlin test support
- Integration tests with Spring Boot Test and Testcontainers
- Separate test application runner for development testing