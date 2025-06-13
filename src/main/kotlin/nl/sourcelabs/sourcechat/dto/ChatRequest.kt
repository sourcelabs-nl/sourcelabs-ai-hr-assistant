package nl.sourcelabs.sourcechat.dto

data class ChatRequest(
    val message: String,
    val sessionId: String? = null
)

data class ChatResponse(
    val message: String,
    val sessionId: String
)