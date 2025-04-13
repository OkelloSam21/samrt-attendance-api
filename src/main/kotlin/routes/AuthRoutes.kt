package routes

import auth.generateAccessToken
import auth.generateRefreshToken
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import models.Staff
import models.Students
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import common.util.AUDIENCE
import common.util.ISSUER
import common.util.SECRET
import java.util.*

val json = Json {
    serializersModule = SerializersModule {
        serializersModule = SerializersModule {
            polymorphic(SignUpRequest::class) {
                subclass(StudentSignUpRequest::class, StudentSignUpRequest.serializer())
                subclass(LecturerSignUpRequest::class, LecturerSignUpRequest.serializer())
                subclass(AdminSignUpRequest::class, AdminSignUpRequest.serializer())
            }
        }
        classDiscriminator = "role"
    }
}

fun Route.authRoutes() {
    post("/auth/signup") {
        val signUpRequest = json.decodeFromString<SignUpRequest>(call.receiveText())

        validateSignUp(
            name = signUpRequest.name,
            email = signUpRequest.email,
            password = signUpRequest.password,
            additionalValidation = {}
        )


        when (signUpRequest.role) {
            UserRole.STUDENT -> {
                if (signUpRequest !is StudentSignUpRequest || signUpRequest.regNo.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Registration number is required for students")
                    return@post
                }
                if (isFieldExists(Students.regNo, signUpRequest.regNo)) {
                    call.respond(HttpStatusCode.Conflict, "A student with this registration number already exists")
                    return@post
                }
            }

            UserRole.LECTURER -> {
                if (signUpRequest !is LecturerSignUpRequest || signUpRequest.employeeId.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Employee ID is required for lecturers")
                    return@post
                }
                if (isFieldExists(Staff.employeeId, signUpRequest.employeeId)) {
                    call.respond(HttpStatusCode.Conflict, "A lecturer with this employee ID already exists")
                    return@post
                }
            }

            UserRole.ADMIN -> {
                if (signUpRequest !is AdminSignUpRequest) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid admin signup request")
                    return@post
                }
            }
        }

        try {
            val userId = newSuspendedTransaction {
                val userId = Users.insertAndGetId {
                    it[name] = signUpRequest.name
                    it[email] = signUpRequest.email
                    it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt())
                    it[role] = signUpRequest.role
                }

                // Insert role-specific data
                when (signUpRequest) {
                    is StudentSignUpRequest -> {
                        Students.insert {
                            it[Students.userId] = userId
                            it[regNo] = signUpRequest.regNo
                        }
                    }

                    is LecturerSignUpRequest -> {
                        Staff.insert {
                            it[Staff.userId] = userId
                            it[employeeId] = signUpRequest.employeeId
                            it[position] = "Lecturer"
                        }
                    }

                    is AdminSignUpRequest -> {
                        Staff.insert {
                            it[Staff.userId] = userId
                            it[employeeId] = "ADM-" + UUID.randomUUID().toString().substring(0, 8)
                            it[position] = "Administrator"
                        }
                    }
                }

                userId
            }

            call.respond(
                HttpStatusCode.Created, mapOf(
                    "message" to "${signUpRequest.role} signed up successfully",
                    "userId" to userId.value.toString()
                )
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create user: ${e.message}")
        }
    }

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
        val role = user[Users.role]
        val accessToken = generateAccessToken(userId, role)
        val refreshToken = generateRefreshToken(userId, role)

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
        val role = decoded.getClaim("role").asString()?.let { UserRole.valueOf(it) }
            ?: throw IllegalArgumentException("role is missing")

        val newAccessToken = generateAccessToken(userId, role)
        val newRefreshToken = generateRefreshToken(userId, role)

        call.respond(mapOf("accessToken" to newAccessToken, "refreshToken" to newRefreshToken))
    }

}

suspend fun validateSignUp(
    name: String,
    email: String,
    password: String,
    additionalValidation: suspend () -> Unit
) {
    // Basic validation
    if (name.isBlank() || email.isBlank() || password.isBlank()) {
        throw BadRequestException("Name, email, and password cannot be blank")
    }

    // Check if email exists
    if (isEmailExists(email)) {
        throw ConflictException("A user with this email already exists")
    }

    // Additional role-specific validation
    additionalValidation()
}



class BadRequestException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)


@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

// Updated request classes
@Serializable
@Polymorphic
sealed class SignUpRequest {
    abstract val name: String
    abstract val email: String
    abstract val password: String
    abstract val role: UserRole
}

@Serializable
@SerialName("STUDENT")
data class StudentSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val regNo: String,
    override val role: UserRole = UserRole.STUDENT
) : SignUpRequest()

@Serializable
@SerialName("LECTURER")
data class LecturerSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val employeeId: String,
    override val role: UserRole = UserRole.LECTURER
) : SignUpRequest()

@Serializable
@SerialName("ADMIN")
data class AdminSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    override val role: UserRole = UserRole.ADMIN
) : SignUpRequest()

// Helper functions
suspend fun isEmailExists(email: String): Boolean {
    return newSuspendedTransaction {
        Users.select { Users.email eq email }.singleOrNull() != null
    }
}

suspend fun <T> isFieldExists(column: Column<T>, value: T): Boolean {
    return newSuspendedTransaction {
        column.table.select { column eq value }.singleOrNull() != null
    }
}
