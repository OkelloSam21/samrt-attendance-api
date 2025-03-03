package auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

suspend fun authorize(call: ApplicationCall, userId: String, vararg allowedRoles: UserRole): Boolean {
    val userRole = transaction {
        Users.select { Users.id eq UUID.fromString(userId) }
            .map { it[Users.role] }
            .singleOrNull()
    }

    if (userRole == null || userRole !in allowedRoles) {
        call.respond(mapOf("error" to "Access denied"))
        return false // Return false to indicate authorization failed
    }
    return true // Return true to indicate authorization succeeded
}