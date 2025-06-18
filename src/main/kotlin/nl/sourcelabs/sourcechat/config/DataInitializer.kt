package nl.sourcelabs.sourcechat.config

import nl.sourcelabs.sourcechat.service.DocumentService
import org.apache.logging.log4j.LogManager
import org.springframework.ai.document.Document
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val documentService: DocumentService
) : ApplicationRunner {
    private val logger = LogManager.getLogger()

    override fun run(args: ApplicationArguments?) {
        try {
            // Initialize the vector store with employee manual content
            addEmployeeManualContent()
            println("✅ Employee manual and hour registration content added to vector store")
        } catch (e: Exception) {
            println("⚠️ Warning: Could not initialize vector store - ${e.message}")
            // Don't fail startup if vector store is not available
        }
    }

    private fun addEmployeeManualContent() {
        logger.info("Loading employee manual content into vector store")
        try {
            val manualFile = javaClass.classLoader.getResource("employee-manual.txt")
            if (manualFile != null) {
                logger.info("Found employee-manual.txt file, loading content")
                val content = manualFile.readText()
                val sections = content.split("\n\n").filter { it.isNotBlank() }
                val documents = sections.mapNotNull { section ->
                    if(documentService.searchSimilarDocuments(section).isNotEmpty()) null
                    else Document(section.trim())
                }
                if(documents.isNotEmpty()) {
                    documentService.addDocuments(documents)
                    logger.info("Successfully loaded {} sections from employee manual", documents.size)
                }
            } else {
                logger.warn("employee-manual.txt not found, using fallback content")
            }
        } catch (e: Exception) {
            logger.error("Failed to load employee manual content: {}", e.message, e)
        }
    }
}