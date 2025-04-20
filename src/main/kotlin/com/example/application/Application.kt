package com.example.application

import com.example.plugins.*
import io.ktor.server.application.*


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