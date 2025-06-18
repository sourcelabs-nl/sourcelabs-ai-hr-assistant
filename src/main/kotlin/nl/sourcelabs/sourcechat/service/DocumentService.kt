package nl.sourcelabs.sourcechat.service

import org.apache.logging.log4j.LogManager
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class DocumentService(
    private val vectorStore: VectorStore
) {
    
    companion object {
        private val logger = LogManager.getLogger(DocumentService::class.java)
    }
    
    fun addDocuments(documents: List<Document>) {
        logger.info("Adding {} documents to vector store", documents.size)
        try {
            vectorStore.add(documents)
            logger.info("Successfully added {} documents to vector store", documents.size)
        } catch (e: Exception) {
            logger.error("Failed to add documents to vector store: {}", e.message, e)
            throw e
        }
    }
    
    fun searchSimilarDocuments(query: String, topK: Int = 5): List<Document> {
        logger.debug("Searching for similar documents - query: '{}', topK: {}", query.take(100), topK)
        try {
            val searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build()
            val results = vectorStore.similaritySearch(searchRequest) ?: emptyList()
            logger.info("Vector search completed - found {} documents", results.size)
            return results
        } catch (e: Exception) {
            logger.error("Vector search failed - query: '{}', error: {}", query.take(100), e.message, e)
            throw e
        }
    }
}