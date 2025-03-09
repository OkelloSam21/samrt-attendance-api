package util

import auth.JwtConfig
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*

fun Application.loadJwtConfig(): JwtConfig {
    // Load .env file using dotenv library
    val dotenv = Dotenv.configure()
        .directory(System.getProperty("user.dir"))
        .ignoreIfMalformed() // Ignore malformed .env files
        .ignoreIfMissing()   // Ignore if .env file is missing (default to environment variables)
        .load()

    val isDevelopment = environment.config.propertyOrNull("ktor.development")?.getString()?.toBoolean() == true

    val secret = dotenv["JWT_SECRET"]
        ?: System.getenv("JWT_SECRET")
        ?: if (isDevelopment) {
            log.warn("JWT_SECRET not set. Using development-only default. DO NOT USE IN PRODUCTION!")
            "development-secret-do-not-use-in-production"
        } else {
            throw IllegalStateException("JWT_SECRET must be set in production mode")
        }

    val issuer = dotenv["JWT_ISSUER"]
        ?: System.getenv("JWT_ISSUER")
        ?: throw IllegalStateException("JWT_ISSUER must be set in production mode")

    val audience = dotenv["JWT_AUDIENCE"]
        ?: System.getenv("JWT_AUDIENCE")
        ?: throw IllegalStateException("JWT_AUDIENCE must be set in production mode")

    val realm = dotenv["JWT_REALM"]
        ?: System.getenv("JWT_REALM")
        ?: throw IllegalStateException("JWT_REALM must be set in production mode")

    return JwtConfig(secret, issuer, audience, realm)
}
