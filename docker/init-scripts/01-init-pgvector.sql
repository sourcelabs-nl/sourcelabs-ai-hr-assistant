-- Initialize PostgreSQL with pgvector extension
-- This script runs automatically when the pgvector container starts

-- Enable the vector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Verify the vector extension is installed
SELECT name, default_version, installed_version 
FROM pg_available_extensions 
WHERE name = 'vector';

-- Create a simple test to ensure vector extension works
DO $$
BEGIN
    -- Test creating a table with vector column
    CREATE TEMP TABLE vector_test (
        id SERIAL PRIMARY KEY,
        embedding vector(3)
    );
    
    -- Test inserting a vector
    INSERT INTO vector_test (embedding) VALUES ('[1,2,3]');
    
    -- Test vector operations
    PERFORM embedding <-> '[1,1,1]' FROM vector_test;
    
    RAISE NOTICE 'pgvector extension is working correctly!';
    
    DROP TABLE vector_test;
END
$$;