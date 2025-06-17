package nl.sourcelabs.sourcechat

import nl.sourcelabs.sourcechat.dto.ChatRequest
import nl.sourcelabs.sourcechat.dto.ChatResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = [
    "spring.sql.init.mode=always" // Disable schema initialization for tests
])
class ChatControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `health endpoint should return OK`() {
        val response = restTemplate.getForEntity("/api/chat/health", Map::class.java)
        
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.get("status") == "OK")
    }

    @Test
    fun `chat endpoint should handle requests gracefully even without valid API key`() {
        val chatRequest = ChatRequest(
            message = "Hello, can you help me with leave hours?",
            sessionId = "test-session"
        )
        
        val response = restTemplate.postForEntity("/api/chat", chatRequest, ChatResponse::class.java)
        
        // Should return a response even if AI service fails
        assert(response.statusCode == HttpStatus.OK || response.statusCode == HttpStatus.BAD_REQUEST)
    }
}