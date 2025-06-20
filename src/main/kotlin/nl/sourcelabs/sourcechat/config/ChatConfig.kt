package nl.sourcelabs.sourcechat.config

import nl.sourcelabs.sourcechat.tools.DateTimeTools
import nl.sourcelabs.sourcechat.tools.HourRegistrationToolService
import org.apache.logging.log4j.LogManager
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer
import org.springframework.ai.rag.retrieval.search.DocumentRetriever
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever
import org.springframework.ai.vectorstore.VectorStore
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
    fun chatMemory(chatMemoryRepository: JdbcChatMemoryRepository): ChatMemory {
        logger.info("Creating in-memory chat memory with max messages: {}", maxMessages)
        return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(maxMessages)
            .build()
    }

    @Bean
    fun queryTransformer(chatModel: ChatModel): QueryTransformer {
        return TranslationQueryTransformer.builder()
            .chatClientBuilder(ChatClient.builder(chatModel))
            .targetLanguage("english")
            .build()
    }

    @Bean
    fun documentRetriever(vectorStore: VectorStore): DocumentRetriever {
        return VectorStoreDocumentRetriever.builder()
            .similarityThreshold(0.5)
            .topK(10)
            .vectorStore(vectorStore)
            .build()
    }

    @Bean
    fun retrievalAugmentationAdvisor(
        documentRetriever: DocumentRetriever,
        queryTransformer: QueryTransformer
    ): RetrievalAugmentationAdvisor {

        val retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
            .documentRetriever(documentRetriever)
            .queryTransformers(queryTransformer)
            .build()

        return retrievalAugmentationAdvisor
    }

    @Bean
    fun chatClient(
        chatModel: ChatModel,
        chatMemory: ChatMemory,
        hourRegistrationToolService: HourRegistrationToolService,
        retrievalAugmentationAdvisor: RetrievalAugmentationAdvisor,
    ): ChatClient {
        val systemPrompt = loadSystemPrompt()
        logger.info("Creating chat client with system prompt loaded from: {}",systemPromptResource?.description)

        return ChatClient.builder(chatModel)
            .defaultSystem(systemPrompt)
            .defaultAdvisors(
                retrievalAugmentationAdvisor,
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                SimpleLoggerAdvisor()
            )
            .defaultTools(hourRegistrationToolService, DateTimeTools())
            .build()
    }
    
    private fun loadSystemPrompt(): String {
        return try {
            systemPromptResource?.inputStream?.bufferedReader()?.use { it.readText() }!!
        } catch (e: Exception) {
            logger.warn("Failed to load system prompt from file")
            throw e
        }
    }
}
