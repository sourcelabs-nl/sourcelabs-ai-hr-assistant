package nl.sourcelabs.sourcechat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SourcechatApplication

fun main(args: Array<String>) {
    runApplication<SourcechatApplication>(*args)
}
