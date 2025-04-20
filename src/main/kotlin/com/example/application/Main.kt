package com.example.application

import com.example.config.AppConfig
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    try {
        AppConfig.logConfiguration()

        // Start the server
        logger.info { "Starting HTTP server on ${AppConfig.server.host}:${AppConfig.server.port}" }
        embeddedServer(
            factory = Netty,
            port = AppConfig.server.port,
            host = AppConfig.server.host
        ) {
            module()
        }.start(wait = true)
    } catch (e: Exception) {
        logger.error(e) { "Failed to start application: ${e.message}" }
    }
}
