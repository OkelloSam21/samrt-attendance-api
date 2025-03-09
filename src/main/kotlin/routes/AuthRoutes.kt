package routes

import auth.generateAccessToken
import auth.generateRefreshToken
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import util.AUDIENCE
import util.ISSUER
import util.SECRET

fun Route.authRoutes() {
    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        val user = transaction {
            Users.select { Users.email eq request.email }.singleOrNull()
        }

        if (user == null || !BCrypt.checkpw(request.password, user[Users.password])) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
            return@post
        }

        val userId = user[Users.id].value.toString()
        val accessToken = generateAccessToken(userId)
        val refreshToken = generateRefreshToken(userId)

        call.respond(mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
    }

    post("/auth/refresh") {
        val request = call.receive<RefreshTokenRequest>()
        val decoded = JWT.require(Algorithm.HMAC256(SECRET))
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .build()
            .verify(request.refreshToken)

        val userId = decoded.getClaim("userId").asString()
        val newAccessToken = generateAccessToken(userId)
        val newRefreshToken = generateRefreshToken(userId)

        call.respond(mapOf("accessToken" to newAccessToken, "refreshToken" to newRefreshToken))
    }
}

data class LoginRequest(val email: String, val password: String)
data class RefreshTokenRequest(val refreshToken: String)