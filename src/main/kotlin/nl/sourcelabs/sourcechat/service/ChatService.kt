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
        logger.info("Starting chat processing - sessionId: {}, message: '{}'", sessionId, request.message.take(100))
        
        try {
            // Save user message
            val userMessage = ChatMessage(
                sessionId = sessionId,
                role = "user",
                content = request.message
            )
            chatMessageRepository.save(userMessage)
            logger.info("Saved user message to database - sessionId: {}", sessionId)
            
            // Search for relevant documents using RAG
            val relevantDocs = documentService.searchSimilarDocuments(request.message, topK = 3)
            logger.info("RAG search completed - sessionId: {}, found {} relevant documents", sessionId, relevantDocs.size)
            
            val contextFromDocs = if (relevantDocs.isNotEmpty()) {
                "Relevant information from employee manual:\n" + 
                relevantDocs.joinToString("\n") { "Reference: ${it.text}" } + "\n\n"
            } else ""
            
            // Build user message with RAG context
            val userMessageWithContext = buildString {
                append(contextFromDocs)
                append("User question: ${request.message}")
            }
            
            // Call AI model with memory advisor
            logger.info("Calling AI model - sessionId: {}, using MessageChatMemoryAdvisor", sessionId)
            val aiResponse = try {
                chatClient.prompt()
                    .advisors { advisorSpec -> 
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId)
                    }
                    .user(userMessageWithContext)
                    .call()
                    .content() ?: run {
                        logger.warn("AI model returned null content - sessionId: {}", sessionId)
                        "I apologize, but I'm unable to provide a response at the moment. Please try again."
                    }
            } catch (e: Exception) {
                logger.error("AI model call failed - sessionId: {}, error: {}", sessionId, e.message, e)
                throw e
            }
            
            logger.info("AI response received - sessionId: {}, responseLength: {}", sessionId, aiResponse.length)

            // Save assistant response
            val assistantMessage = ChatMessage(
                sessionId = sessionId,
                role = "assistant",
                content = aiResponse
            )
            chatMessageRepository.save(assistantMessage)
            logger.info("Saved assistant message to database - sessionId: {}", sessionId)
            
            val response = ChatResponse(
                message = aiResponse,
                sessionId = sessionId
            )
            
            logger.info("Chat processing completed successfully - sessionId: {}", sessionId)
            return response
            
        } catch (e: Exception) {
            logger.error("Chat processing failed - sessionId: {}, error: {}", sessionId, e.message, e)
            throw e
        }
    }
    
    fun getChatHistory(sessionId: String): List<ChatMessage> {
        logger.info("Retrieving chat history - sessionId: {}", sessionId)
        try {
            val history = chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
            logger.info("Chat history retrieved - sessionId: {}, messageCount: {}", sessionId, history.size)
            return history
        } catch (e: Exception) {
            logger.error("Failed to retrieve chat history - sessionId: {}, error: {}", sessionId, e.message, e)
            throw e
        }
    }
}