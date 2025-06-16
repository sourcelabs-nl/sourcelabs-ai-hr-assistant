package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.repository.ChatMessageRepository
import org.apache.logging.log4j.LogManager
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatService(
    private val chatClient: ChatClient,
    private val chatMessageRepository: ChatMessageRepository,
    private val documentService: DocumentService
) {

    private val logger = LogManager.getLogger()
    
    fun chat(request: ChatRequest): ChatResponse {
        val sessionId = request.sessionId ?: UUID.randomUUID().toString()
        
        // Save user message
        val userMessage = ChatMessage(
            sessionId = sessionId,
            role = "user",
            content = request.message
        )
        chatMessageRepository.save(userMessage)
        
        // Search for relevant documents using RAG
        val relevantDocs = documentService.searchSimilarDocuments(request.message, topK = 3)
        val contextFromDocs = if (relevantDocs.isNotEmpty()) {
            "Relevant information from employee manual:\n" + 
            relevantDocs.joinToString("\n") { "Reference: ${it.text}" } + "\n\n"
        } else ""
        
        // Build user message with RAG context
        val userMessageWithContext = buildString {
            append(contextFromDocs)
            append("User question: ${request.message}")
        }
        
        val aiResponse = try {
            chatClient.prompt()
                .advisors { advisorSpec -> 
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId)
                }
                .user(userMessageWithContext)
                .call()
                .content() ?: "I apologize, but I'm unable to provide a response at the moment. Please try again."
        } catch (e: Exception) {
            logger.warn(e)
            throw e
        }

        // Save assistant response
        val assistantMessage = ChatMessage(
            sessionId = sessionId,
            role = "assistant",
            content = aiResponse
        )
        chatMessageRepository.save(assistantMessage)
        
        return ChatResponse(
            message = aiResponse,
            sessionId = sessionId
        )
    }
    
    fun getChatHistory(sessionId: String): List<ChatMessage> {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
    }
}