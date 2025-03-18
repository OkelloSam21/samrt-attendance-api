package application

import auth.configureAuthentication
import configureCors
import db.DatabaseFactory
import io.ktor.http.*
import plugins.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import routes.ConflictException
import routes.LecturerSignUpRequest
import routes.SignUpRequest
import routes.StudentSignUpRequest
import util.AppConfig

fun main() {

    val serverPort = AppConfig.port ?: 8080
    embeddedServer(Netty, port = serverPort, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Install content negotiation
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                polymorphic(SignUpRequest::class) {
                    subclass(
                        StudentSignUpRequest::class,
                        StudentSignUpRequest.serializer()
                    )
                    subclass(
                        LecturerSignUpRequest::class,
                        LecturerSignUpRequest.serializer()
                    )
                }
            }
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }

    install(StatusPages) {
        exception<SerializationException> { call, cause ->
            call.respondText(text = cause.message ?: "Invalid request body", status = HttpStatusCode.BadRequest)
        }
        exception<BadRequestException> { call, cause ->
            call.respondText(cause.message ?: "Bad Request", ContentType.Text.Plain, HttpStatusCode.BadRequest)
        }
        exception<ConflictException> { call, cause ->
            call.respondText(cause.message ?: "Conflict", ContentType.Text.Plain, HttpStatusCode.Conflict)
        }
        exception<Throwable> { call, cause ->
            cause.printStackTrace() // Log unexpected exceptions
            call.respondText("An unexpected error occurred", ContentType.Text.Plain, HttpStatusCode.InternalServerError)
        }
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

    configureAuthentication()
    configureCors()
    configureSwagger()
    configureRouting()
    configureRouting()
}