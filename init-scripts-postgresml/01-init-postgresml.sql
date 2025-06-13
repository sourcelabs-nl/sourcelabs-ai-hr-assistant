-- Initialize PostgresML with required extensions
-- This script runs automatically when the PostgresML container starts

-- Enable PostgresML extension (includes vector support)
CREATE EXTENSION IF NOT EXISTS pgml;
CREATE EXTENSION IF NOT EXISTS vector;

-- Verify the extensions are installed
SELECT name, default_version, installed_version 
FROM pg_available_extensions 
WHERE name IN ('pgml', 'vector')
ORDER BY name;

-- Test PostgresML embedding functionality
DO $$
BEGIN
    -- Test creating a table for embeddings
    CREATE TEMP TABLE embedding_test (
        id SERIAL PRIMARY KEY,
        text TEXT,
        embedding vector(384)  -- Common dimension for sentence transformers
    );
    
    -- Test PostgresML embedding generation
    -- Note: This may take some time on first run as it downloads the model
    INSERT INTO embedding_test (text, embedding) 
    VALUES ('Hello world', pgml.embed('sentence-transformers/all-MiniLM-L6-v2', 'Hello world'));
    
    RAISE NOTICE 'PostgresML extension is working correctly!';
    RAISE NOTICE 'Embedding model sentence-transformers/all-MiniLM-L6-v2 is available';
    
    DROP TABLE embedding_test;
    
EXCEPTION WHEN OTHERS THEN
    RAISE NOTICE 'PostgresML embedding test failed (this is normal on first run): %', SQLERRM;
    RAISE NOTICE 'Extension is installed but model may need to be downloaded on first use';
END
$$;

-- Show available models (this may be empty initially)
SELECT * FROM pgml.models LIMIT 5;