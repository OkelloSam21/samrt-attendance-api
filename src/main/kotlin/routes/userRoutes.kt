package routes

import auth.authorize
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import models.Users
import models.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Route.userRoutes() {
    route("/users") {

        // LOGIN API
        post("/login") {
            val request = call.receive<Map<String, String>>()
            val email = request["email"] ?: return@post call.respond(mapOf("error" to "Email is required"))
            val password = request["password"] ?: return@post call.respond(mapOf("error" to "Password is required"))

            val user = transaction {
                Users.select { Users.email eq email }.singleOrNull()
            }

            if (user == null) {
                call.respond(mapOf("error" to "User not found"))
                return@post
            }

            val storedHashedPassword = user[Users.password]
            if (!BCrypt.checkpw(password, storedHashedPassword)) {
                call.respond(mapOf("error" to "Invalid credentials"))
                return@post
            }

            call.respond(mapOf("message" to "Login successful", "userId" to user[Users.id].value.toString()))
        }

        // SIGNUP API
        post("/signup") {
            val request = call.receive<Map<String, String>>()
            val name = request["name"] ?: return@post call.respond(mapOf("error" to "Name is required"))
            val email = request["email"] ?: return@post call.respond(mapOf("error" to "Email is required"))
            val password = request["password"] ?: return@post call.respond(mapOf("error" to "Password is required"))
            val role = request["role"]?.let { UserRole.valueOf(it.uppercase()) } ?: UserRole.STUDENT

            val existingUser = transaction {
                Users.select { Users.email eq email }.singleOrNull()
            }
            if (existingUser != null) {
                call.respond(mapOf("error" to "Email already in use"))
                return@post
            }

            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
            val userId = UUID.randomUUID()

            transaction {
                Users.insert {
                    it[id] = userId
                    it[this.name] = name
                    it[this.email] = email
                    it[this.password] = hashedPassword
                    it[this.role] = role
                    it[createdAt] = java.time.Instant.now()
                }
            }
            call.respond(mapOf("message" to "User registered successfully", "userId" to userId.toString()))
        }

        // GET ALL USERS (Restricted to ADMIN)
        get {
            val adminId = call.request.queryParameters["admin_id"]
                ?: return@get call.respond(mapOf("error" to "Admin ID required"))

            if (!authorize(call, adminId, UserRole.ADMIN)) {
                return@get // Return early if authorization failed
            }

            val users = transaction {
                Users.selectAll().map {
                    mapOf(
                        "id" to it[Users.id].value.toString(),
                        "name" to it[Users.name],
                        "email" to it[Users.email],
                        "role" to it[Users.role].name
                    )
                }
            }
            call.respond(users)
        }

        // GET USER PROFILE (Restricted to Owner & Admin)
        get("/{id}") {
            val userId = call.parameters["id"] ?: return@get call.respond(mapOf("error" to "User ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@get call.respond(mapOf("error" to "Requester ID required"))

            // Call authorize as a regular function
            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER, UserRole.STUDENT)) {
                return@get // Return early if authorization failed
            }

            val user = transaction {
                Users.select { Users.id eq UUID.fromString(userId) }
                    .map {
                        mapOf(
                            "id" to it[Users.id].value.toString(),
                            "name" to it[Users.name],
                            "email" to it[Users.email],
                            "role" to it[Users.role].name
                        )
                    }
                    .singleOrNull()
            }

            if (user == null) {
                call.respond(mapOf("error" to "User not found"))
            } else {
                call.respond(user)
            }
        }

        // UPDATE USER PROFILE (Restricted to Admin & Owner)
        put("/{id}") {
            val userId = call.parameters["id"] ?: return@put call.respond(mapOf("error" to "User ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@put call.respond(mapOf("error" to "Requester ID required"))

            // Call authorize as a regular function
            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@put // Return early if authorization failed
            }

            val request = call.receive<Map<String, String>>()
            val name = request["name"] ?: return@put call.respond(mapOf("error" to "Name is required"))
            val email = request["email"] ?: return@put call.respond(mapOf("error" to "Email is required"))
            val password = request["password"] ?: return@put call.respond(mapOf("error" to "Password is required"))
            val role = request["role"]?.let { UserRole.valueOf(it.uppercase()) }
                ?: return@put call.respond(mapOf("error" to "Role is required"))

            val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

            val updatedRows = transaction {
                Users.update({ Users.id eq UUID.fromString(userId) }) {
                    it[this.name] = name
                    it[this.email] = email
                    it[this.password] = hashedPassword
                    it[this.role] = role
                }
            }

            if (updatedRows == 0) {
                call.respond(mapOf("error" to "User not found"))
            } else {
                call.respond(mapOf("message" to "User updated successfully"))
            }
        }

        // DELETE USER (Restricted to ADMIN)
        delete("/{id}") {
            val userId = call.parameters["id"] ?: return@delete call.respond(mapOf("error" to "User ID is required"))
            val adminId = call.request.queryParameters["admin_id"]
                ?: return@delete call.respond(mapOf("error" to "Admin ID required"))

            // Call authorize as a regular function
            if (!authorize(call, adminId, UserRole.ADMIN)) {
                return@delete // Return early if authorization failed
            }

            val deletedRows = transaction {
                Users.deleteWhere { Users.id eq UUID.fromString(userId) }
            }

            if (deletedRows == 0) {
                call.respond(mapOf("error" to "User not found"))
            } else {
                call.respond(mapOf("message" to "User deleted successfully"))
            }
        }
    }
}