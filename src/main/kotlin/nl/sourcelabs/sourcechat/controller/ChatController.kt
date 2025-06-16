package nl.sourcelabs.sourcechat.controller

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.service.ChatService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = ["*"])
class ChatController(
    private val chatService: ChatService
) {
    
    private val logger = LogManager.getLogger()
    
    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
        logger.info("REST API: Chat request received - sessionId: {}, hasMessage: {}", 
            request.sessionId ?: "new", request.message.isNotEmpty())
        
        val response = chatService.chat(request)
        logger.info("REST API: Chat response sent - sessionId: {}", response.sessionId)
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/history/{sessionId}")
    fun getChatHistory(@PathVariable sessionId: String): ResponseEntity<List<ChatMessage>> {
        logger.info("REST API: Chat history request - sessionId: {}", sessionId)
        
        val history = chatService.getChatHistory(sessionId)
        logger.info("REST API: Chat history response - sessionId: {}, messageCount: {}", sessionId, history.size)
        return ResponseEntity.ok(history)
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        logger.info("REST API: Health check requested")
        return ResponseEntity.ok(mapOf("status" to "OK", "service" to "ChatController"))
    }
}