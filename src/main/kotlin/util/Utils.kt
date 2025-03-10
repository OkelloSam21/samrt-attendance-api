package util

import auth.JwtConfig
import io.ktor.server.application.Application

// Lazy initialization for configuration to ensure loadJwtConfig runs properly
private lateinit var jwtConfig: JwtConfig

fun initializeJwtConfig(application: Application) {
    jwtConfig = application.loadJwtConfig()
}

// Reuse the configuration loaded via loadJwtConfig
val SECRET: String get() = jwtConfig.secret
val ISSUER: String get() = jwtConfig.issuer
val AUDIENCE: String get() = jwtConfig.audience

const val ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000 // 15 minutes
const val REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000 // 7 days