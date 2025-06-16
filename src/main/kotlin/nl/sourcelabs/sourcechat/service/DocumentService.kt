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
    
    fun addEmployeeManualContent() {
        logger.info("Loading employee manual content into vector store")
        try {
            val manualFile = javaClass.classLoader.getResource("employee-manual.txt")
            if (manualFile != null) {
                logger.info("Found employee-manual.txt file, loading content")
                val content = manualFile.readText()
                val sections = content.split("\n\n").filter { it.isNotBlank() }
                val documents = sections.map { section ->
                    Document(section.trim())
                }
                vectorStore.add(documents)
                logger.info("Successfully loaded {} sections from employee manual", documents.size)
            } else {
                logger.warn("employee-manual.txt not found, using fallback content")
                val fallbackContent = listOf(
                    Document("Annual leave policy: Employees are entitled to 25 days of annual leave per year. Leave must be requested at least 2 weeks in advance."),
                    Document("Sick leave policy: Employees can take up to 10 days of sick leave per year. Medical certificate required for absences longer than 3 consecutive days."),
                    Document("Billable hours policy: All client work must be accurately recorded and billed. Time should be recorded in 15-minute increments."),
                    Document("Travel reimbursement: Mileage reimbursement €0.35 per kilometer for car travel, €0.10 per kilometer for bike travel."),
                    Document("Working from home: Maximum 3 days per week working from home with manager approval. Company laptop provided for remote work.")
                )
                vectorStore.add(fallbackContent)
                logger.info("Successfully loaded {} fallback policy documents", fallbackContent.size)
            }
        } catch (e: Exception) {
            logger.error("Failed to load employee manual content: {}", e.message, e)
            val fallbackContent = listOf(
                Document("Employee manual content could not be loaded. Please contact HR for policy information.")
            )
            vectorStore.add(fallbackContent)
            logger.warn("Added error fallback document due to loading failure")
        }
    }
    
    fun searchSimilarDocuments(query: String, topK: Int = 5): List<Document> {
        logger.info("Searching for similar documents - query: '{}', topK: {}", query.take(100), topK)
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