package nl.sourcelabs.sourcechat.config

import nl.sourcelabs.sourcechat.service.DocumentService
import org.springframework.ai.document.Document
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val documentService: DocumentService
) : ApplicationRunner {
    
    override fun run(args: ApplicationArguments?) {
        try {
            // Initialize the vector store with employee manual content
            documentService.addEmployeeManualContent()
            
            // Add hour registration related content
            val hourRegistrationDocs = listOf(
                Document("Hour registration: Employees must register their leave hours through the system. Leave requests require manager approval."),
                Document("Billable hours: All billable client work must be logged with client name, location, hours worked, and description. Travel expenses should include kilometers for car/bike travel."),
                Document("Leave types: Annual leave (25 days), sick leave (10 days), personal leave, maternity/paternity leave, bereavement leave."),
                Document("Billable hour tracking: Include project name, work location, travel type (car, bike, public transport, flight, train), and travel distance when applicable."),
                Document("Hour approval process: Leave hours require manager approval. Billable hours go through submitted -> approved -> invoiced workflow.")
            )
            
            documentService.addDocuments(hourRegistrationDocs)
            println("✅ Employee manual and hour registration content added to vector store")
        } catch (e: Exception) {
            println("⚠️ Warning: Could not initialize vector store - ${e.message}")
            // Don't fail startup if vector store is not available
        }
    }
}