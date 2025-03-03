package application

import configureCors
import db.DatabaseFactory
import plugins.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Install content negotiation
    install(ContentNegotiation) {
        json()
    }

    // Initialize database connection BEFORE configuring routes
    try {
        println("Initializing database connection...")
        DatabaseFactory.init()
        println("Database initialized successfully!")
    } catch (e: Exception) {
        println("CRITICAL ERROR: Database initialization failed: ${e.message}")
        e.printStackTrace()
    }

    configureCors()
    configureSwagger()
    configureRouting()
    configureRouting()
}