package routes

import auth.authorize
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import util.SECRET
import java.util.*


fun Route.userRoutes() {
    route("/users") {
        val secret = SECRET

        // Get all users (Admin-only access)
        get {
            if (!authorize(call, secret, UserRole.ADMIN)) return@get

            val users = transaction {
                Users.selectAll()
                    .map { row ->
                        mapOf(
                            "id" to row[Users.id].value.toString(),
                            "email" to row[Users.email],
                            "role" to row[Users.role].toString()
                        )
                    }
            }

            call.respond(users)
        }

        // Get a specific user (Admin-only access)
        get("/{id}") {
//            if (!authorize(call, secret, UserRole.ADMIN)) return@get

            val id = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            val user = transaction {
                Users.select { Users.id eq id }.limit(1).singleOrNull()
            }

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                call.respond(
                    mapOf(
                        "id" to user[Users.id].value.toString(),
                        "name" to user[Users.name],
                        "email" to user[Users.email],
                        "role" to user[Users.role].toString(),
                        "regNo" to user[Users.regNo],
                        "employeeRoleNo" to user[Users.employeeRoleNo]
                    )
                )
            }
        }

        // Create a new user (Admin-only access)
        post ("/create"){
            if (!authorize(call, secret, UserRole.ADMIN)) return@post

            val request = call.receive<CreateUserRequest>()

            transaction {
                Users.insert {
                    it[email] = request.email
                    it[password] = BCrypt.hashpw(request.password, BCrypt.gensalt())
                    it[role] = request.role
                }
            }

            call.respond(HttpStatusCode.Created, "User created successfully")
        }

        // Delete a specific user (Admin-only access)
        delete("/delete/{id}") {
            if (!authorize(call, secret, UserRole.ADMIN)) return@delete

            val id = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

            transaction {
                Users.deleteWhere { Users.id eq id }
            }

            call.respond(HttpStatusCode.OK, "User deleted successfully")
        }
    }
}

@Serializable
data class CreateUserRequest(val email: String, val password: String, val role: UserRole)
