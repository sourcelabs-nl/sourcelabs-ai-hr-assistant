package nl.sourcelabs.sourcechat.config

import nl.sourcelabs.sourcechat.mcp.HourRegistrationMcpService
import org.apache.logging.log4j.LogManager
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
class ChatConfig {
    
    companion object {
        private val logger = LogManager.getLogger(ChatConfig::class.java)
    }
    
    @Value("\${app.chat.memory.max-messages:20}")
    private val maxMessages: Int = 20
    
    @Value("\${app.chat.system-prompt-file:classpath:system-prompt.txt}")
    private val systemPromptResource: Resource? = null
    
    @Bean
    fun chatMemory(): ChatMemory {
        logger.info("Creating chat memory with max messages: {}", maxMessages)
        return MessageWindowChatMemory.builder()
            .maxMessages(maxMessages)
            .build()
    }
    
    @Bean
    fun chatClient(
        @Qualifier("ollamaChatModel") chatModel: ChatModel,
        hourRegistrationMcpService: HourRegistrationMcpService,
        chatMemory: ChatMemory,
        vectorStore: VectorStore
    ): ChatClient {
        val systemPrompt = loadSystemPrompt()
        logger.info("Creating chat client with system prompt loaded from: {}", 
            systemPromptResource?.description ?: "default")
        
        return ChatClient.builder(chatModel)
            .defaultSystem(systemPrompt)
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore).build()
            )
            .defaultTools(hourRegistrationMcpService)
            .build()
    }
    
    private fun loadSystemPrompt(): String {
        return try {
            systemPromptResource?.inputStream?.bufferedReader()?.use { it.readText() }
                ?: getDefaultSystemPrompt()
        } catch (e: Exception) {
            logger.warn("Failed to load system prompt from file, using default: {}", e.message)
            getDefaultSystemPrompt()
        }
    }
    
    private fun getDefaultSystemPrompt(): String = """
        You are the Sourcelabs HR assistant. You provide information about leave hours, billable client hours and the employee manual.
        
        You have access to tools that allow you to directly register hours and retrieve hour summaries for employees. When users ask to register hours or get information about their hours, use the available tools to help them.
        
        Use today's date as reference when users say "today", "yesterday", etc.
        Be helpful and guide users through the process step by step. Always use the tools to complete hour registration requests.
    """.trimIndent()
}
