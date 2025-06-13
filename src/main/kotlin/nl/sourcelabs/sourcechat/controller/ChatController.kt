package nl.sourcelabs.sourcechat.controller

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import nl.sourcelabs.sourcechat.entity.ChatMessage
import nl.sourcelabs.sourcechat.service.ChatService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = ["*"])
class ChatController(
    private val chatService: ChatService
) {
    
    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ResponseEntity<ChatResponse> {
        return try {
            val response = chatService.chat(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ChatResponse(
                    message = "Error processing your request: ${e.message}",
                    sessionId = request.sessionId ?: "error"
                )
            )
        }
    }
    
    @GetMapping("/history/{sessionId}")
    fun getChatHistory(@PathVariable sessionId: String): ResponseEntity<List<ChatMessage>> {
        return try {
            val history = chatService.getChatHistory(sessionId)
            ResponseEntity.ok(history)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(emptyList())
        }
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "OK", "service" to "ChatController"))
    }
}