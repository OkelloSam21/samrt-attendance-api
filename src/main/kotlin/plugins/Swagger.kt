package plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Configure Swagger documentation
 */
fun Application.configureSwagger() {
    logger.info { "Configuring Swagger UI..." }

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "2.0"
        }
    }
}