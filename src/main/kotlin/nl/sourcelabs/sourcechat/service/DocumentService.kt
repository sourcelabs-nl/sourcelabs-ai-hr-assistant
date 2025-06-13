package nl.sourcelabs.sourcechat.service

import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class DocumentService(
    private val vectorStore: VectorStore
) {
    
    fun addDocuments(documents: List<Document>) {
        vectorStore.add(documents)
    }
    
    fun addEmployeeManualContent() {
        try {
            val manualFile = javaClass.classLoader.getResource("employee-manual.txt")
            if (manualFile != null) {
                val content = manualFile.readText()
                val sections = content.split("\n\n").filter { it.isNotBlank() }
                val documents = sections.map { section ->
                    Document(section.trim())
                }
                vectorStore.add(documents)
            } else {
                // Fallback content if file not found
                val fallbackContent = listOf(
                    Document("Annual leave policy: Employees are entitled to 25 days of annual leave per year. Leave must be requested at least 2 weeks in advance."),
                    Document("Sick leave policy: Employees can take up to 10 days of sick leave per year. Medical certificate required for absences longer than 3 consecutive days."),
                    Document("Billable hours policy: All client work must be accurately recorded and billed. Time should be recorded in 15-minute increments."),
                    Document("Travel reimbursement: Mileage reimbursement €0.35 per kilometer for car travel, €0.10 per kilometer for bike travel."),
                    Document("Working from home: Maximum 3 days per week working from home with manager approval. Company laptop provided for remote work.")
                )
                vectorStore.add(fallbackContent)
            }
        } catch (e: Exception) {
            // Fallback in case of any errors
            val fallbackContent = listOf(
                Document("Employee manual content could not be loaded. Please contact HR for policy information.")
            )
            vectorStore.add(fallbackContent)
        }
    }
    
    fun searchSimilarDocuments(query: String, topK: Int = 5): List<Document> {
        val searchRequest = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .build()
        return vectorStore.similaritySearch(searchRequest) ?: emptyList()
    }
}