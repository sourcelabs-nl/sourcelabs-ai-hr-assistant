package nl.sourcelabs.sourcechat.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

data class ChatRequest(
    @field:NotBlank(message = "Message cannot be blank")
    @field:Size(max = 4000, message = "Message too long")
    val message: String,
    
    @field:Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "Invalid session ID format")
    val sessionId: String? = null
)

data class ChatResponse(
    @field:NotBlank
    val message: String,
    
    @field:NotBlank  
    val sessionId: String,
    
    val timestamp: Instant = Instant.now()
)