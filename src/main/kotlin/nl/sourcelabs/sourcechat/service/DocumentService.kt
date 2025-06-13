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
        // Sample employee manual content for HR assistant
        val manualContent = listOf(
            Document("Annual leave policy: Employees are entitled to 25 days of annual leave per year. Leave must be requested at least 2 weeks in advance through the HR system."),
            Document("Sick leave policy: Employees can take up to 10 days of sick leave per year without a doctor's note. Extended sick leave requires medical documentation."),
            Document("Working hours: Standard working hours are 40 hours per week, Monday to Friday 9 AM to 5 PM. Flexible working arrangements can be discussed with management."),
            Document("Overtime policy: Overtime work must be pre-approved by management. Overtime is compensated at 1.5x regular hourly rate."),
            Document("Remote work policy: Employees may work remotely up to 2 days per week with manager approval. Home office setup support is available."),
            Document("Billable hours: Client-facing work must be tracked and logged daily. Billable hour targets are set quarterly by project managers."),
            Document("Time tracking: All employees must log their hours daily in the time tracking system. Hours should be categorized by project and activity type."),
            Document("Expense reimbursement: Business expenses can be submitted for reimbursement with proper receipts within 30 days of incurrence."),
            Document("Training and development: Each employee has an annual training budget of €1000 for professional development courses and conferences."),
            Document("Performance reviews: Performance reviews are conducted bi-annually in June and December. Goal setting and feedback sessions occur quarterly.")
        )
        
        vectorStore.add(manualContent)
    }
    
    fun searchSimilarDocuments(query: String, topK: Int = 5): List<Document> {
        val searchRequest = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .build()
        return vectorStore.similaritySearch(searchRequest) ?: emptyList()
    }
}