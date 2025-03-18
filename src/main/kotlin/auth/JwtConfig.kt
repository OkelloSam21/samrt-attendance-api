package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import models.UserRole
import util.*
import java.util.*


data class JwtConfig(
    val secret: String,
    val issuer: String = "",
    val audience: String = "",
    val realm: String = ""
)

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = AppConfig.jwtRealm
            verifier(
//                for the secrete use the one specified inside the .env file

                JWT.require(Algorithm.HMAC256(AppConfig.jwtSecret))
                    .withIssuer(AppConfig.jwtIssuer)
                    .withAudience(AppConfig.jwtAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            @Serializable
            data class ErrorResponse(val error: String)

            challenge { _, _ ->
                call.application.environment.log.warn("Unauthorized access attempt")

                call.application.launch {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid token"))
                }
            }
        }
    }
}

fun generateAccessToken(userId: String,role:UserRole): String {
    return JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("userId", userId)
        .withClaim("role", role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .sign(Algorithm.HMAC256(SECRET))
}

fun generateRefreshToken(userId: String, role: UserRole): String {
    return JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("userId", userId)
        .withClaim("role", role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .sign(Algorithm.HMAC256(SECRET))
}

