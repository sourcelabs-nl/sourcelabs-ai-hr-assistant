# SourceChat - HR Assistant

A Spring Boot AI-powered chat application built with Spring AI and Ollama, designed to assist with HR-related queries about leave hours, billable client hours, and employee manual information.

## Features

- ✅ **Ollama Integration** - Uses llama3.2 for intelligent chat responses and nomic-embed-text for embeddings
- ✅ **Chat Memory** - MessageChatMemoryAdvisor with 20-message sliding window for conversation continuity
- ✅ **RAG (Retrieval Augmented Generation)** - Uses pgvector for similarity search on employee manual content
- ✅ **React Frontend** - Modern Material UI chat interface built with TypeScript
- ✅ **REST API** - RESTful endpoints for chat interactions and hour registration
- ✅ **MCP Support** - Model Context Protocol server and client for hour registration tools
- ✅ **Hour Registration** - Complete leave hours and billable hours tracking system

## Tech Stack

- **Backend**: Spring Boot 3.5.0, Kotlin 1.9.25, Java 21
- **AI**: Spring AI 1.0.0 with Ollama (llama3.2 + nomic-embed-text)
- **Database**: PostgreSQL with pgvector extension
- **Data Layer**: Spring Data JDBC
- **Frontend**: React 18.2.0, TypeScript, Material UI
- **Build**: Maven with automated frontend build
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
```

## Development

### Build
```bash
./mvnw clean compile
```

### Run Tests
```bash
./mvnw test
```

### Package
```bash
./mvnw clean package
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
│ (Material UI)   │───▶│  (Controller)   │───▶│  (Spring AI)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  PostgreSQL     │    │  Ollama         │
                              │  + pgvector     │    │  llama3.2 +     │
                              │  (RAG Storage)  │    │  nomic-embed    │
                              └─────────────────┘    └─────────────────┘
                                        │                       │
                                        ▼                       ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  Hour Tracking  │    │  Memory Store   │
                              │  (Leave/Bill)   │    │  (20 Messages)  │
                              └─────────────────┘    └─────────────────┘
                                        │                       │
                                        ▼                       ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  MCP Tools      │    │  Chat Memory    │
                              │  (Registration) │    │  (Per Session)  │
                              └─────────────────┘    └─────────────────┘
```

## Chat Memory & Conversation Continuity

The application uses Spring AI's `MessageChatMemoryAdvisor` to maintain conversation context:

- **Sliding Window**: Retains last 20 messages per conversation session
- **Session-based**: Each chat session has isolated memory using sessionId
- **Automatic Management**: Spring AI handles message retrieval and context injection
- **Combined with RAG**: Memory works alongside document search for comprehensive responses

This ensures the HR assistant remembers previous interactions within a session, providing contextual responses like:
- "You mentioned earlier..." 
- "Based on your previous request..."
- "Following up on the leave hours you registered..."

## Employee Manual Content

The application comes pre-loaded with sample HR policies including:
- Annual leave policy (25 days)
- Sick leave policy (10 days)
- Working hours and overtime
- Remote work policy
- Billable hours tracking
- Training and development budget
- Performance review schedule

## Example Queries

Once the application is running, try these example interactions:

- "Register 8 hours of annual leave for today"
- "Log 6 hours of work for client Acme Corp in Amsterdam today"
- "How many leave days do I have left this year?"
- "Show me my recent billable hours"
- "What's the company policy on working from home?"
- "Register sick leave for yesterday, 8 hours"

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

## Contributing

1. Follow existing code style and conventions
2. Write tests for new functionality
3. Update documentation as needed
4. Ensure all tests pass before committing

## License

[Add your license information here]