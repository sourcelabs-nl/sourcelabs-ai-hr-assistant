# SourceChat - HR Assistant

A Spring Boot AI-powered chat application built with Spring AI and Ollama, designed to assist with HR-related queries about leave hours, billable client hours, and employee manual information.

## Features

- ✅ **Ollama Integration** - Uses llama3.2 for intelligent chat responses and nomic-embed-text for embeddings
- ✅ **Chat Memory** - MessageChatMemoryAdvisor with configurable message window for conversation continuity
- ✅ **RAG (Retrieval Augmented Generation)** - Uses pgvector for similarity search on employee manual content
- ✅ **React Frontend** - Modern Material UI chat interface built with TypeScript
- ✅ **REST API** - RESTful endpoints with comprehensive input validation and error handling
- ✅ **MCP Support** - Model Context Protocol server and client for hour registration tools
- ✅ **Hour Registration** - Complete leave hours and billable hours tracking system
- ✅ **Input Validation** - Jakarta Bean Validation with custom constraints and detailed error messages
- ✅ **Security** - Configurable CORS, input sanitization, and path validation
- ✅ **Error Handling** - RFC 7807 compliant ProblemDetail responses with structured error information
- ✅ **Configuration Management** - Externalized configuration with environment-specific settings

## Tech Stack

- **Backend**: Spring Boot 3.5.0, Kotlin 1.9.25, Java 21
- **AI**: Spring AI 1.0.0 with Ollama (llama3.2 + nomic-embed-text)
- **Database**: PostgreSQL with pgvector extension
- **Data Layer**: Spring Data JDBC with transactional support
- **Frontend**: React 18.2.0, TypeScript, Material UI
- **Build**: Maven with automated frontend build
- **Validation**: Jakarta Bean Validation with custom constraints
- **Logging**: Log4j2 with structured logging and consistent patterns
- **Error Handling**: GlobalExceptionHandler with ProblemDetail (RFC 7807) responses
- **Security**: Configurable CORS, input validation, path sanitization
- **Configuration**: Externalized properties with environment-specific overrides
- **Testing**: Testcontainers, JUnit 5
- **Containerization**: Docker Compose for PostgreSQL and Ollama services

## Quick Start

### Prerequisites
- Java 21+
- Docker and Docker Compose
- Ollama installed locally

### 1. Setup Ollama Models
```bash
# Pull required models
./setup-ollama-models.sh
```

### 2. Start Docker Services
```bash
# Start PostgreSQL and Ollama services
cd docker
docker-compose up -d
```

### 3. Build and Run the Application
```bash
# Build frontend and backend
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

### 4. Access the UI
Open your browser to: http://localhost:8080

## API Endpoints

### Chat with AI
```http
POST /api/chat
Content-Type: application/json

{
  "message": "Register 8 hours of leave for today",
  "sessionId": "optional-session-id"
}
```

### Get Chat History
```http
GET /api/chat/history/{sessionId}
```

### Health Check
```http
GET /api/chat/health
```

### Hour Registration (REST API)
```http
# Register leave hours
POST /api/hours/leave
Content-Type: application/json

{
  "employeeId": "emp123",
  "leaveType": "ANNUAL_LEAVE",
  "startDate": "2024-01-15",
  "endDate": "2024-01-15",
  "totalHours": 8,
  "description": "Vacation day"
}

# Register billable hours
POST /api/hours/billable
Content-Type: application/json

{
  "employeeId": "emp123",
  "clientName": "Acme Corp",
  "location": "Amsterdam",
  "workDate": "2024-01-15",
  "hoursWorked": 8,
  "workDescription": "Development work"
}
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Ollama Chat Configuration (llama3.2 on port 1234)
spring.ai.ollama.chat.base-url=http://localhost:1234
spring.ai.ollama.chat.options.model=llama3.2
spring.ai.ollama.chat.options.temperature=0.7

# Ollama Embeddings Configuration (nomic-embed-text on port 11434)
spring.ai.ollama.embedding.base-url=http://localhost:11434
spring.ai.ollama.embedding.options.model=nomic-embed-text

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/sourcechat
spring.datasource.username=sourcechat
spring.datasource.password=sourcechat

# Vector Store (PGVector) - 768 dimensions for nomic-embed-text
spring.ai.vectorstore.pgvector.dimensions=768
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE

# MCP Server Configuration
spring.ai.mcp.server.name=sourcelabs-hr-server
spring.ai.mcp.server.capabilities.tool=true

# Application Configuration
app.chat.memory.max-messages=20
app.chat.system-prompt-file=classpath:system-prompt.txt
app.cors.allowed-origins=http://localhost:3000,http://localhost:8080
app.vector-store.initialize=true
app.vector-store.fail-on-error=false
```

### Environment-Specific Configuration

The application supports environment-specific configuration through Spring profiles and property overrides:

- **CORS Origins**: Configure `app.cors.allowed-origins` for your deployment environment
- **Chat Memory**: Adjust `app.chat.memory.max-messages` based on memory requirements
- **System Prompt**: Customize `app.chat.system-prompt-file` to load different prompts
- **Vector Store**: Control initialization with `app.vector-store.initialize` and error handling

## Development

### Build
```bash
# Compile with validation checks
./mvnw clean compile

# Compile with validation and run frontend build
./mvnw clean package
```

### Run Tests
```bash
# Run all tests including integration tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=SourcechatApplicationTests

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test
```

### Package
```bash
# Create executable JAR with frontend
./mvnw clean package

# Skip frontend build (faster for backend-only changes)
./mvnw clean package -Dexec.skip=true
```

### Code Quality
```bash
# Run with validation enabled
./mvnw clean compile -Dspring.profiles.active=dev

# Check for compilation issues
./mvnw clean compile -q
```

### Docker Services
```bash
# Start all services (from docker directory)
cd docker
docker-compose up -d

# Start with pgAdmin (optional)
docker-compose --profile dev up -d

# Stop services
docker-compose down
```

### Frontend Development
```bash
# Build frontend manually
./build-frontend.sh

# Frontend development (from src/main/frontend)
cd src/main/frontend
npm install
npm start
```

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React UI      │    │  REST API       │    │  Chat Service   │
│ (Material UI)   │───▶│  (Validated)    │───▶│  (Spring AI)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                    ┌─────────────────┐    ┌─────────────────┐
                    │ Input Validation│    │ Exception       │
                    │ (Bean Valid.)   │    │ Handling        │
                    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  PostgreSQL     │    │  Ollama         │
                              │  + pgvector     │    │  llama3.2 +     │
                              │  (RAG Storage)  │    │  nomic-embed    │
                              └─────────────────┘    └─────────────────┘
                                        │                       │
                                        ▼                       ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  Hour Tracking  │    │  Memory Store   │
                              │  (Transactional)│    │  (Configurable) │
                              └─────────────────┘    └─────────────────┘
                                        │                       │
                                        ▼                       ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  MCP Tools      │    │  Chat Memory    │
                              │  (Validated)    │    │  (Per Session)  │
                              └─────────────────┘    └─────────────────┘
```

### Key Architectural Components

**API Layer:**
- Controllers with comprehensive input validation
- CORS configuration for secure cross-origin requests
- Structured error responses following RFC 7807

**Service Layer:**
- Transactional business logic with proper boundaries
- Specific exception types for different error categories
- Business validation beyond basic input constraints

**Data Layer:**
- Spring Data JDBC with optimized queries
- PostgreSQL with pgvector for efficient similarity search
- Proper entity relationships and constraints

**Configuration:**
- Externalized application properties
- Environment-specific configuration support
- Configurable chat memory and system prompts

## Chat Memory & Conversation Continuity

The application uses Spring AI's `MessageChatMemoryAdvisor` to maintain conversation context:

- **Configurable Window**: Retains configurable number of messages per session (default: 20)
- **Session-based**: Each chat session has isolated memory using validated sessionId
- **Automatic Management**: Spring AI handles message retrieval and context injection
- **Combined with RAG**: Memory works alongside document search for comprehensive responses
- **Error Recovery**: Graceful handling of memory failures with proper fallbacks

**Configuration:**
```properties
# Adjust memory window size
app.chat.memory.max-messages=20

# Customize system prompt
app.chat.system-prompt-file=classpath:system-prompt.txt
```

This ensures the HR assistant remembers previous interactions within a session, providing contextual responses like:
- "You mentioned earlier..." 
- "Based on your previous request..."
- "Following up on the leave hours you registered..."

**Session Management:**
- Session IDs are validated using regex patterns for security
- Memory is isolated per session to prevent data leakage
- Automatic session generation when not provided
- Proper error handling for invalid or expired sessions

## Employee Manual Content

The application comes pre-loaded with sample HR policies including:
- Annual leave policy (25 days)
- Sick leave policy (10 days)
- Working hours and overtime
- Remote work policy
- Billable hours tracking
- Training and development budget
- Performance review schedule

## Code Quality & Validation Features

### Input Validation
The application implements comprehensive input validation using Jakarta Bean Validation:

**DTO Validation:**
```kotlin
data class ChatRequest(
    @field:NotBlank(message = "Message cannot be blank")
    @field:Size(max = 4000, message = "Message too long")
    val message: String,
    
    @field:Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "Invalid session ID format")
    val sessionId: String? = null
)
```

**Controller Validation:**
```kotlin
@PostMapping
fun chat(@Valid @RequestBody request: ChatRequest): ResponseEntity<ChatResponse>

@GetMapping("/history/{sessionId}")
fun getChatHistory(
    @PathVariable @Pattern(regexp = "^[a-zA-Z0-9-_]+$") sessionId: String
): ResponseEntity<List<ChatMessage>>
```

**Business Logic Validation:**
- Date range validation (end date >= start date)
- Hour limits (positive values, max 24 hours per day)
- Travel information consistency checks
- Employee ID format validation

### Exception Handling Strategy

**Exception Hierarchy:**
```kotlin
// Base exception for service layer
abstract class ServiceException(message: String, cause: Throwable? = null)

// Specific exceptions for different domains
class ChatServiceException(message: String, cause: Throwable? = null)
class HourRegistrationException(message: String, cause: Throwable? = null)
class BusinessValidationException(message: String, cause: Throwable? = null)
```

**GlobalExceptionHandler Features:**
- Method argument validation errors with field-specific messages
- Constraint violation errors with property path information
- Service-specific error handling with appropriate HTTP status codes
- RFC 7807 compliant ProblemDetail responses
- Structured error logging with full context

### Security & Configuration

**CORS Configuration:**
```properties
# Configure allowed origins for your environment
app.cors.allowed-origins=http://localhost:3000,http://localhost:8080
```

**Input Sanitization:**
- Path variable validation with regex patterns
- Request body validation with size and format constraints
- SQL injection prevention through parameterized queries
- XSS prevention through proper input encoding

**Configuration Management:**
```properties
# Chat configuration
app.chat.memory.max-messages=20
app.chat.system-prompt-file=classpath:system-prompt.txt

# Vector store configuration
app.vector-store.initialize=true
app.vector-store.fail-on-error=false
```

## Example Queries

Once the application is running, try these example interactions:

### Hour Registration
- "Register 8 hours of annual leave for today"
- "Log 6 hours of work for client Acme Corp in Amsterdam today"
- "Register sick leave for yesterday, 8 hours"
- "Log 4 hours for client TechCorp, traveled by car from Utrecht to Amsterdam, 45km"

### Hour Tracking
- "How many leave days do I have left this year?"
- "Show me my recent billable hours"
- "What's my total billable hours for 2024?"
- "Show me my leave history"

### Policy Questions
- "What's the company policy on working from home?"
- "How much annual leave am I entitled to?"
- "What's the travel reimbursement rate?"
- "Do I need a medical certificate for sick leave?"

### Data Validation Examples
The application now validates input and provides helpful error messages:
- Hours must be positive and not exceed 24 per day
- Employee IDs must follow alphanumeric format
- Dates must be valid and in correct ranges
- Travel information must be consistent (type required if kilometers specified)

## Troubleshooting

### Ollama Model Issues
If chat responses fail:
1. Ensure Ollama models are pulled: `./setup-ollama-models.sh`
2. Verify Ollama services are running on correct ports (1234 for chat, 11434 for embeddings)
3. Check Docker containers: `docker ps`

### Vector Store Issues
If you encounter vector store errors:
1. PostgreSQL is running with pgvector extension
2. Database connection properties are correct
3. Vector dimensions match (768 for nomic-embed-text)

### Frontend Build Issues
- Run `./build-frontend.sh` manually if Maven build fails
- Check Node.js and npm are installed for frontend development
- Verify React dependencies in `src/main/frontend/package.json`

### Database Connection
- Verify PostgreSQL is running on port 5432
- Check database credentials in `application.properties`
- Ensure database `sourcechat` exists with proper schema
- Validate pgvector extension is installed and enabled

### Configuration Issues
- Check `app.chat.memory.max-messages` is a valid positive integer
- Verify `app.cors.allowed-origins` contains your frontend URL
- Ensure `app.chat.system-prompt-file` points to an accessible resource
- Validate vector store initialization settings

### Validation Errors
- Review error responses for specific validation failures
- Check that request bodies include all required fields
- Verify path variables match expected regex patterns
- Ensure numeric values are within acceptable ranges

### Input Validation and Error Handling

The application implements comprehensive input validation and error handling:

**Validation Features:**
- **Request Validation**: All API endpoints validate input using Jakarta Bean Validation
- **Path Variables**: Employee IDs and session IDs validated with regex patterns
- **Business Logic**: Custom validation for date ranges, hour limits, and travel information
- **Security**: Input sanitization to prevent XSS and injection attacks

**Error Response Format (RFC 7807 ProblemDetail):**
```json
{
  "type": "about:blank",
  "title": "Validation failed", 
  "status": 400,
  "detail": "Please check the provided values",
  "timestamp": "2025-06-16T10:00:00Z",
  "path": "/api/chat",
  "validation_errors": {
    "message": "Message cannot be blank",
    "totalHours": "Total hours must be positive"
  }
}
```

**Exception Types:**
- `ChatServiceException`: Chat processing errors
- `HourRegistrationException`: Hour registration failures
- `BusinessValidationException`: Business rule violations
- `DocumentServiceException`: Vector store and document errors

### Logging and Debugging
The application provides structured logging for troubleshooting:

**Log Levels and Patterns:**
- **INFO**: Normal application flow and successful operations
- **WARN**: Recoverable issues and business validation failures
- **ERROR**: Exceptions and system errors with full stack traces
- **DEBUG**: Detailed debugging information (disabled by default)

**Key Log Messages:**
- `Processing chat - sessionId: {}, message: '{}'` - Chat requests with context
- `MCP Tool: {toolName} called - employeeId: {}, ...` - Tool executions with parameters
- `Register leave hours request - employeeId: {}, leaveType: {}, hours: {}` - API operations
- `RAG search found {} relevant documents` - Vector search results
- `Validation failed: {}` - Input validation errors

**Structured Logging:**
```
2025-06-16T12:44:04.026+02:00 INFO [main] n.s.s.c.ChatController : Chat request received - sessionId: new, messageLength: 45
2025-06-16T12:44:04.125+02:00 ERROR [main] n.s.s.s.ChatService : Chat failed - sessionId: abc123
nl.sourcelabs.sourcechat.exception.ChatServiceException: AI service unavailable
```

## Contributing

### Code Standards
1. **Kotlin Style**: Follow Kotlin coding conventions and idiomatic patterns
2. **Validation**: Add appropriate `@Valid` annotations and constraint validations
3. **Error Handling**: Use specific exception types and proper error messages
4. **Logging**: Use companion objects for loggers and structured logging patterns
5. **Configuration**: Externalize configurable values to `application.properties`

### Development Workflow
1. **Testing**: Write comprehensive tests for new functionality
2. **Validation**: Ensure all input validation and error handling is implemented
3. **Documentation**: Update README.md and inline documentation
4. **Code Review**: Ensure all tests pass and code follows established patterns
5. **Security**: Validate all user inputs and implement proper CORS configuration

### Code Quality Checklist
- [ ] Input validation with Jakarta Bean Validation annotations
- [ ] Proper exception handling with specific exception types
- [ ] Structured logging with appropriate log levels
- [ ] Configuration externalized to properties files
- [ ] Unit and integration tests covering new functionality
- [ ] Documentation updated to reflect changes
- [ ] Security considerations addressed (CORS, input sanitization)
- [ ] Transaction boundaries properly defined for data operations

## License

[Add your license information here]