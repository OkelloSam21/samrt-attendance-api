package application

import io.ktor.server.application.*
import plugins.*


fun Application.module() {
    // Install plugins
    configureContentNegotiation()
    configureStatusPages()
    configureAuthentication()
    configureCors()
    configureSwagger()

    // Register routes
    configureRouting()
}