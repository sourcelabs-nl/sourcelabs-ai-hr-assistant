package nl.sourcelabs.sourcechat.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatConfig {
    
    @Bean
    fun chatClient(chatModel: ChatModel): ChatClient {
        return ChatClient.builder(chatModel)
            .defaultSystem("You are the Sourcelabs HR assistant. You provide information about leave hours, billable client hours and the employee manual.")
            .build()
    }
}