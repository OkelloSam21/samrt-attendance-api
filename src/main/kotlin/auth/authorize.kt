package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

suspend fun authorize(call: ApplicationCall, secret: String, vararg allowedRoles: UserRole): Boolean {
    val token = extractToken(call) ?: return unauthorized(call, "Authorization token is required")

    val decodedJWT = validateToken(token, secret) ?: return unauthorized(call, "Invalid or expired token")

    val userId = decodedJWT.getClaim("userId")?.asString()
        ?: return unauthorized(call, "Token is missing userId")
    val userRole = decodedJWT.getClaim("role")?.asString()?.let { UserRole.valueOf(it) }
        ?: return unauthorized(call, "Token is missing or has an invalid role")

    if (userRole !in allowedRoles) {
        return unauthorized(call, "Insufficient permissions")
    }

    if (!isUserExists(userId, userRole)) {
        return unauthorized(call, "User does not exist or has an invalid role")
    }

    return true
}

private suspend fun extractToken(call: ApplicationCall): String? {
    val authHeader = call.request.headers["Authorization"] ?: return null
    if (!authHeader.startsWith("Bearer ")) return null
    return authHeader.removePrefix("Bearer ")
}

private fun validateToken(token: String, secret: String): com.auth0.jwt.interfaces.DecodedJWT? {
    return try {
        JWT.require(Algorithm.HMAC256(secret))
            .build()
            .verify(token)
    } catch (e: Exception) {
        null
    }
}

private fun isUserExists(userId: String, expectedRole: UserRole): Boolean {
    return transaction {
        Users.select { (Users.id eq UUID.fromString(userId)) and  (Users.role eq expectedRole) }.empty().not()
    }
}

private suspend fun unauthorized(call: ApplicationCall, message: String): Boolean {
    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to message))
    return false
}