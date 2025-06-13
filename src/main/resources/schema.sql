-- Create chat_messages table for Spring Data JDBC
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on session_id for better query performance
CREATE INDEX IF NOT EXISTS idx_chat_messages_session_id ON chat_messages(session_id);

-- Create index on timestamp for ordering
CREATE INDEX IF NOT EXISTS idx_chat_messages_timestamp ON chat_messages(timestamp);

-- Create leave_hours table
CREATE TABLE IF NOT EXISTS leave_hours (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(255) NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_hours DECIMAL(8,2) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by VARCHAR(255)
);

-- Create indexes for leave_hours
CREATE INDEX IF NOT EXISTS idx_leave_hours_employee_id ON leave_hours(employee_id);
CREATE INDEX IF NOT EXISTS idx_leave_hours_start_date ON leave_hours(start_date);
CREATE INDEX IF NOT EXISTS idx_leave_hours_status ON leave_hours(status);

-- Create billable_client_hours table
CREATE TABLE IF NOT EXISTS billable_client_hours (
    id BIGSERIAL PRIMARY KEY,
    employee_id VARCHAR(255) NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    project_name VARCHAR(255),
    location VARCHAR(255) NOT NULL,
    work_date DATE NOT NULL,
    hours_worked DECIMAL(8,2) NOT NULL,
    description TEXT NOT NULL,
    travel_type VARCHAR(50),
    travel_kilometers DECIMAL(8,2),
    travel_from_location VARCHAR(255),
    travel_to_location VARCHAR(255),
    hourly_rate DECIMAL(10,2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    invoiced_at TIMESTAMP
);

-- Create indexes for billable_client_hours
CREATE INDEX IF NOT EXISTS idx_billable_hours_employee_id ON billable_client_hours(employee_id);
CREATE INDEX IF NOT EXISTS idx_billable_hours_client_name ON billable_client_hours(client_name);
CREATE INDEX IF NOT EXISTS idx_billable_hours_work_date ON billable_client_hours(work_date);
CREATE INDEX IF NOT EXISTS idx_billable_hours_status ON billable_client_hours(status);

-- Enable pgvector extension for vector operations (for RAG)
CREATE EXTENSION IF NOT EXISTS vector;

-- Create vector_store table for Spring AI pgvector
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT NOT NULL,
    metadata JSONB,
    embedding VECTOR(768)  -- Ollama nomic-embed-text dimension
);

-- Create index for vector similarity search
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx ON vector_store 
USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);