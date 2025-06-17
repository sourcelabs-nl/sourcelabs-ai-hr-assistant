package nl.sourcelabs.sourcechat.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.service.ChatService
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = ["\${app.cors.allowed-origins:http://localhost:3000}"])
@Validated
class ChatController(
    private val chatService: ChatService
) {
    
    companion object {
        private val logger = LogManager.getLogger(ChatController::class.java)
    }
    
    @PostMapping
    fun chat(@Valid @RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
        logger.info("Chat request received - sessionId: {}, messageLength: {}", 
            request.sessionId ?: "new", request.message.length)
        
        val response = chatService.chat(request)
        logger.info("Chat response sent - sessionId: {}", response.sessionId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        logger.debug("Health check requested")
        return ResponseEntity.ok(mapOf(
            "status" to "OK", 
            "service" to "ChatController",
            "timestamp" to System.currentTimeMillis()
        ))
    }
}