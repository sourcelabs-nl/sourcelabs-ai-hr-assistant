package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.exception.ChatServiceException
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
    
    companion object {
        private val logger = LogManager.getLogger(ChatService::class.java)
        private const val MAX_MESSAGE_PREVIEW = 100
        private const val DEFAULT_RAG_TOP_K = 3
    }
    
    fun chat(request: ChatRequest): ChatResponse {
        val sessionId = request.sessionId ?: generateSessionId()
        logger.info("Processing chat - sessionId: {}, message: '{}'", 
            sessionId, request.message.take(MAX_MESSAGE_PREVIEW))
        
        return try {
            val userMessage = saveUserMessage(request, sessionId)
            val aiResponse = generateAiResponse(request, sessionId)
            saveAssistantMessage(aiResponse, sessionId)
            
            ChatResponse(message = aiResponse, sessionId = sessionId)
                .also { logger.info("Chat completed - sessionId: {}", sessionId) }
        } catch (e: Exception) {
            logger.error("Chat failed - sessionId: {}", sessionId, e)
            throw ChatServiceException("Failed to process chat request", e)
        }
    }
    
    private fun generateSessionId(): String = UUID.randomUUID().toString()
    
    private fun saveUserMessage(request: ChatRequest, sessionId: String): ChatMessage {
        return ChatMessage(
            sessionId = sessionId,
            role = MessageRole.USER.value,
            content = request.message
        ).let { message ->
            chatMessageRepository.save(message)
                .also { logger.debug("User message saved - sessionId: {}", sessionId) }
        }
    }
    
    private fun generateAiResponse(request: ChatRequest, sessionId: String): String {
        val contextualMessage = buildContextualMessage(request.message)
        
        return try {
            chatClient.prompt()
                .advisors { advisorSpec -> 
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId)
                }
                .user(contextualMessage)
                .call()
                .content()
                ?: throw ChatServiceException("AI model returned null response")
        } catch (e: Exception) {
            logger.error("AI model call failed - sessionId: {}", sessionId, e)
            throw ChatServiceException("AI service unavailable", e)
        }
    }
    
    private fun buildContextualMessage(message: String): String {
        val relevantDocs = documentService.searchSimilarDocuments(message, DEFAULT_RAG_TOP_K)
        logger.debug("RAG search found {} relevant documents", relevantDocs.size)
        
        return buildString {
            if (relevantDocs.isNotEmpty()) {
                appendLine("Relevant information from employee manual:")
                relevantDocs.forEach { doc ->
                    appendLine("Reference: ${doc.text}")
                }
                appendLine()
            }
            append("User question: $message")
        }
    }
    
    private fun saveAssistantMessage(aiResponse: String, sessionId: String): ChatMessage {
        return ChatMessage(
            sessionId = sessionId,
            role = MessageRole.ASSISTANT.value,
            content = aiResponse
        ).let { message ->
            chatMessageRepository.save(message)
                .also { logger.debug("Assistant message saved - sessionId: {}", sessionId) }
        }
    }
    
    fun getChatHistory(sessionId: String): List<ChatMessage> {
        logger.info("Retrieving chat history - sessionId: {}", sessionId)
        return try {
            val history = chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId)
            logger.info("Chat history retrieved - sessionId: {}, count: {}", sessionId, history.size)
            history
        } catch (e: Exception) {
            logger.error("Failed to retrieve chat history - sessionId: {}", sessionId, e)
            throw ChatServiceException("Failed to retrieve chat history", e)
        }
    }
}

enum class MessageRole(val value: String) {
    USER("user"),
    ASSISTANT("assistant")
}