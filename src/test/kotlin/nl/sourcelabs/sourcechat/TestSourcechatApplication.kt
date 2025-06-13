package nl.sourcelabs.sourcechat

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<SourcechatApplication>().with(TestcontainersConfiguration::class).run(*args)
}
