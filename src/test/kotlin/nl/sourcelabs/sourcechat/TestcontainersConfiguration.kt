package nl.sourcelabs.sourcechat

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withDatabaseName("sourcechat")
            .withUsername("sourcechat")
            .withPassword("sourcechat")
            .withInitScript("init-scripts/01-init-extensions.sql")
    }
}
