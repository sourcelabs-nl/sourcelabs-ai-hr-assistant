# SourceChat - HR Assistant

A Spring Boot AI-powered chat application built with Spring AI and Anthropic Claude, designed to assist with HR-related queries about leave hours, billable client hours, and employee manual information.

## Features

- ✅ **Anthropic Claude Integration** - Uses Claude 3.5 Sonnet for intelligent responses
- ✅ **Chat Memory** - Stores conversation history in PostgreSQL using Spring Data JDBC
- ✅ **RAG (Retrieval Augmented Generation)** - Uses pgvector for similarity search on employee manual content
- ✅ **Modern UI** - Clean chat interface accessible via web browser
- ✅ **REST API** - RESTful endpoints for chat interactions
- ⚠️ **MCP Support** - Model Context Protocol for hour registration system (partially implemented)

## Tech Stack

- **Backend**: Spring Boot 3.5.0, Kotlin 1.9.25, Java 21
- **AI**: Spring AI 1.0.0 with Anthropic Claude
- **Database**: PostgreSQL with pgvector extension
- **Data Layer**: Spring Data JDBC
- **Frontend**: HTML/CSS/JavaScript
- **Testing**: Testcontainers, JUnit 5

## Quick Start

### Prerequisites
- Java 21+
- Docker and Docker Compose
- Anthropic API key

### 1. Start PostgreSQL with pgvector
```bash
docker-compose up -d postgres
```

### 2. Configure API Key
Update `src/main/resources/application.properties`:
```properties
spring.ai.anthropic.api-key=YOUR_ACTUAL_ANTHROPIC_API_KEY
```

### 3. Run the Application
```bash
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
  "message": "How many leave days do I have?",
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

## Configuration

Key configuration properties in `application.properties`:

```properties
# Anthropic Claude Configuration
spring.ai.anthropic.api-key=YOUR_API_KEY
spring.ai.anthropic.chat.options.model=claude-3-5-sonnet-latest
spring.ai.anthropic.chat.options.temperature=0.7

# PostgreSQL Database
spring.datasource.url=jdbc:postgresql://localhost:5432/sourcechat
spring.datasource.username=sourcechat
spring.datasource.password=sourcechat

# Vector Store (PGVector)
spring.ai.vectorstore.pgvector.host=localhost
spring.ai.vectorstore.pgvector.port=5432
spring.ai.vectorstore.pgvector.database=sourcechat
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
# Start all services
docker-compose up -d

# Start with pgAdmin (optional)
docker-compose --profile dev up -d

# Stop services
docker-compose down
```

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web UI        │    │  REST API       │    │  Chat Service   │
│  (HTML/JS)      │───▶│  (Controller)   │───▶│  (Spring AI)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                              ┌─────────────────┐    ┌─────────────────┐
                              │  PostgreSQL     │    │  Anthropic      │
                              │  + pgvector     │    │  Claude API     │
                              │  (RAG + Memory) │    │                 │
                              └─────────────────┘    └─────────────────┘
```

## Employee Manual Content

The application comes pre-loaded with sample HR policies including:
- Annual leave policy (25 days)
- Sick leave policy (10 days)
- Working hours and overtime
- Remote work policy
- Billable hours tracking
- Training and development budget
- Performance review schedule

## Troubleshooting

### Vector Store Issues
If you encounter vector store errors, ensure:
1. PostgreSQL is running with pgvector extension
2. Database connection properties are correct
3. Vector extension is properly initialized

### API Key Issues
- Ensure your Anthropic API key is valid
- Check API key has sufficient credits
- Verify network connectivity to Anthropic API

### Database Connection
- Verify PostgreSQL is running on port 5432
- Check database credentials
- Ensure database `sourcechat` exists

## Contributing

1. Follow existing code style and conventions
2. Write tests for new functionality
3. Update documentation as needed
4. Ensure all tests pass before committing

## License

[Add your license information here]