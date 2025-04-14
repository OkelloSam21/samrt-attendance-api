package application

import config.AppConfig
import di.AppInjector
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Application entry point
 */
fun main() {
    try {
        // Log startup
        logger.info { "Starting Smart Attendance API..." }

        // Initialize database
        logger.info { "Initializing database..." }
        AppInjector.databaseFactory.init()
        logger.info { "Database initialized successfully" }

        // Log configuration
        AppConfig.logConfiguration()

        // Start the server using the module function
        logger.info { "Starting HTTP server on ${AppConfig.server.host}:${AppConfig.server.port}" }
        embeddedServer(
            factory = Netty,
            port = AppConfig.server.port,
            host = AppConfig.server.host,
            module = Application::module
        ).start(wait = true)
    } catch (e: Exception) {
        logger.error(e) { "Failed to start application: ${e.message}" }
    }
}
