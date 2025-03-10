package routes

import auth.generateAccessToken
import auth.generateRefreshToken
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import util.AUDIENCE
import util.ISSUER
import util.SECRET

fun Route.authRoutes() {
    post("/auth/signup/student") {
        val signUpRequest = call.receive<StudentSignUpRequest>()

        // Validate the role
        if (signUpRequest.role != UserRole.STUDENT) {
            call.respond(HttpStatusCode.BadRequest, "Invalid role: Role must be STUDENT for this endpoint")
            return@post
        }

        // Validate fields
        validateSignUp(
            name = signUpRequest.name,
            email = signUpRequest.email,
            password = signUpRequest.password
        ) {
            validateStudentFields(signUpRequest.regNo)
        }

        // Perform Database Insertion
        newSuspendedTransaction {
            Users.insert {
                it[name] = signUpRequest.name
                it[email] = signUpRequest.email
                it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt())
                it[role] = signUpRequest.role
                it[regNo] = signUpRequest.regNo
                it[employeeRoleNo] = null
            }
        }

        call.respond(HttpStatusCode.Created, "Student signed up successfully")
    }

    post("/auth/signup/lecturer") {
        val signUpRequest = call.receive<LecturerSignUpRequest>()

        // Validate the role
        if (signUpRequest.role != UserRole.LECTURER) {
            call.respond(HttpStatusCode.BadRequest, "Invalid role: Role must be LECTURER for this endpoint")
            return@post
        }

        // Validate fields
        validateSignUp(
            name = signUpRequest.name,
            email = signUpRequest.email,
            password = signUpRequest.password
        ) {
            validateLecturerFields(signUpRequest.employeeRoleNo)
        }

        // Perform Database Insertion
        newSuspendedTransaction {
            Users.insert {
                it[name] = signUpRequest.name
                it[email] = signUpRequest.email
                it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt())
                it[role] = signUpRequest.role
                it[regNo] = null
                it[employeeRoleNo] = signUpRequest.employeeRoleNo
            }
        }

        call.respond(HttpStatusCode.Created, "Lecturer signed up successfully")
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

suspend fun validateStudentFields(regNo: String) {
    if (regNo.isBlank()) {
        throw BadRequestException("Registration number cannot be blank")
    }
    if (isFieldExists(Users.regNo, regNo)) {
        throw ConflictException("A student with this registration number already exists")
    }
}

suspend fun isEmailExists(email: String): Boolean {
    return newSuspendedTransaction {
        Users.select { Users.email eq email }.singleOrNull() != null
    }
}

suspend fun validateLecturerFields(employeeRoleNo: String) {
    if (employeeRoleNo.isBlank()) {
        throw BadRequestException("Employee role number cannot be blank")
    }
    if (isFieldExists(Users.employeeRoleNo, employeeRoleNo)) {
        throw ConflictException("A lecturer with this employee role number already exists")
    }
}

class BadRequestException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)

suspend fun isFieldExists(field: Column<String?>, value: String): Boolean {
    return newSuspendedTransaction {
        Users.select { field eq value }.singleOrNull() != null
    }
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
sealed class SignUpRequest {
    abstract val name: String
    abstract val email: String
    abstract val password: String
    abstract val role: UserRole
}

@Serializable
data class StudentSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    override val role: UserRole = UserRole.STUDENT,
    val regNo: String
) : SignUpRequest()

@Serializable
data class LecturerSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    override val role: UserRole = UserRole.LECTURER,
    val employeeRoleNo: String
) : SignUpRequest()
