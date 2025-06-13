package nl.sourcelabs.sourcechat.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("chat_messages")
data class ChatMessage(
    @Id
    val id: Long? = null,
    val sessionId: String,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)