package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.repository.ChatMessageRepository
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatService(
    private val chatClient: ChatClient,
    private val chatMessageRepository: ChatMessageRepository,
    private val documentService: DocumentService,
    private val hourRegistrationService: HourRegistrationService
) {
    
    fun chat(request: ChatRequest): ChatResponse {
        val sessionId = request.sessionId ?: UUID.randomUUID().toString()
        
        // Save user message
        val userMessage = ChatMessage(
            sessionId = sessionId,
            role = "user",
            content = request.message
        )
        chatMessageRepository.save(userMessage)
        
        // Get chat history for context
        val chatHistory = chatMessageRepository.findTop10BySessionIdOrderByTimestampDesc(sessionId)
            .reversed() // Get chronological order
        
        // Search for relevant documents using RAG
        val relevantDocs = documentService.searchSimilarDocuments(request.message, topK = 3)
        val contextFromDocs = relevantDocs.joinToString("\n") { "Reference: ${it.text}" }
        
        // Build conversation context for AI
        val conversationHistory = buildString {
            if (relevantDocs.isNotEmpty()) {
                append("Relevant information from employee manual:\n")
                append(contextFromDocs)
                append("\n\n")
            }
            
            if (chatHistory.isNotEmpty()) {
                append("Previous conversation:\n")
                chatHistory.forEach { message ->
                    append("${message.role}: ${message.content}\n")
                }
                append("\n")
            }
            append("Current user message: ${request.message}")
        }
        
        val aiResponse = chatClient.prompt()
            .user(conversationHistory)
            .call()
            .content() ?: "I apologize, but I'm unable to provide a response at the moment. Please try again."
        
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