package nl.sourcelabs.sourcechat.repository

import nl.sourcelabs.sourcechat.entity.ChatMessage
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : CrudRepository<ChatMessage, Long> {
    
    @Query("SELECT * FROM chat_messages WHERE session_id = :sessionId ORDER BY timestamp ASC")
    fun findBySessionIdOrderByTimestampAsc(sessionId: String): List<ChatMessage>
    
    @Query("SELECT * FROM chat_messages WHERE session_id = :sessionId ORDER BY timestamp DESC LIMIT 10")
    fun findTop10BySessionIdOrderByTimestampDesc(sessionId: String): List<ChatMessage>
}