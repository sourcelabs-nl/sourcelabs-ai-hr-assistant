package nl.sourcelabs.sourcechat.mcp

import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class McpConfiguration {

    @Bean
    fun hourRegistrationToolCallbackProvider(hourRegistrationMcpService: HourRegistrationMcpService): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(hourRegistrationMcpService)
            .build()
    }
}