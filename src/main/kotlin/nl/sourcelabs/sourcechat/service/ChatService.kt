package nl.sourcelabs.sourcechat.service

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.exception.ChatServiceException
import org.apache.logging.log4j.LogManager
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatService(
    private val chatClient: ChatClient
) {
    
    companion object {
        private val logger = LogManager.getLogger(ChatService::class.java)
        private const val MAX_MESSAGE_PREVIEW = 100
    }
    
    fun chat(request: ChatRequest): ChatResponse {
        val sessionId = request.sessionId ?: generateSessionId()
        logger.info("Processing chat - sessionId: {}, message: '{}'", 
            sessionId, request.message.take(MAX_MESSAGE_PREVIEW))
        
        return try {
            val aiResponse = generateAiResponse(request, sessionId)

            ChatResponse(message = aiResponse, sessionId = sessionId)
                .also { logger.info("Chat completed - sessionId: {}", sessionId) }
        } catch (e: Exception) {
            logger.error("Chat failed - sessionId: {}", sessionId, e)
            throw ChatServiceException("Failed to process chat request", e)
        }
    }
    
    private fun generateSessionId(): String = UUID.randomUUID().toString()
    
    private fun generateAiResponse(request: ChatRequest, sessionId: String): String {
        return try {
            val call = chatClient.prompt()
                .advisors { advisorSpec ->
                    advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId)
                }
                .user(request.message)
                .call()

            call.content()
                ?: throw ChatServiceException("AI model returned null response")
        } catch (e: Exception) {
            logger.error("AI model call failed - sessionId: {}", sessionId, e)
            throw ChatServiceException("AI service unavailable", e)
        }
    }
}