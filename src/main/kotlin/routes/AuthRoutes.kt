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
import util.AUDIENCE
import util.ISSUER
import util.SECRET
import java.time.LocalDateTime
import java.util.*

fun Route.authRoutes() {
//    post("/auth/signup/admin") {
//        val signUpRequest = call.receive<AdminSignUpRequest>()
//
//        // Validate the role
//        if (signUpRequest.role != UserRole.ADMIN) {
//            call.respond(HttpStatusCode.BadRequest, "Invalid role: Role must be ADMIN for this endpoint")
//            return@post
//        }
//
//        // Validate fields
//        validateSignUp(
//            name = signUpRequest.name,
//            email = signUpRequest.email,
//            password = signUpRequest.password
//        ) {
//            // Add any admin-specific validations here if necessary
//            validateAdminFields()
//        }
//
//        // Perform Database Insertion
//        newSuspendedTransaction {
//            Users.insert {
//                it[name] = signUpRequest.name
//                it[email] = signUpRequest.email
//                it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt()) // Hash password
//                it[role] = signUpRequest.role
//                it[regNo] = null   // Admins don’t have "registration number"
//                it[employeeRoleNo] = null // Admins don’t have "employee role number"
//            }
//        }
//
//        call.respond(HttpStatusCode.Created, "Admin signed up successfully")
//    }
//
//    post("/auth/signup/student") {
//        val signUpRequest = call.receive<StudentSignUpRequest>()
//
//        // Validate the role
//        if (signUpRequest.role != UserRole.STUDENT) {
//            call.respond(HttpStatusCode.BadRequest, "Invalid role: Role must be STUDENT for this endpoint")
//            return@post
//        }
//
//        // Validate fields
//        validateSignUp(
//            name = signUpRequest.name,
//            email = signUpRequest.email,
//            password = signUpRequest.password
//        ) {
//            validateStudentFields(signUpRequest.regNo)
//        }
//
//        // Perform Database Insertion
//        newSuspendedTransaction {
//            Users.insert {
//                it[name] = signUpRequest.name
//                it[email] = signUpRequest.email
//                it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt())
//                it[role] = signUpRequest.role
//                it[regNo] = signUpRequest.regNo
//                it[employeeRoleNo] = null
//            }
//        }
//
//        call.respond(HttpStatusCode.Created, "Student signed up successfully")
//    }
//
//    post("/auth/signup/lecturer") {
//        val signUpRequest = call.receive<LecturerSignUpRequest>()
//
//        // Validate the role
//        if (signUpRequest.role != UserRole.LECTURER) {
//            call.respond(HttpStatusCode.BadRequest, "Invalid role: Role must be LECTURER for this endpoint")
//            return@post
//        }
//
//        // Validate fields
//        validateSignUp(
//            name = signUpRequest.name,
//            email = signUpRequest.email,
//            password = signUpRequest.password
//        ) {
//            validateLecturerFields(signUpRequest.employeeRoleNo)
//        }
//
//        // Perform Database Insertion
//        newSuspendedTransaction {
//            Users.insert {
//                it[name] = signUpRequest.name
//                it[email] = signUpRequest.email
//                it[password] = BCrypt.hashpw(signUpRequest.password, BCrypt.gensalt())
//                it[role] = signUpRequest.role
//                it[regNo] = null
//                it[employeeRoleNo] = signUpRequest.employeeRoleNo
//            }
//        }
//
//        call.respond(HttpStatusCode.Created, "Lecturer signed up successfully")
//    }

    post("/auth/signup") {
        val signUpRequest = call.receive<SignUpRequest>()

        // Validate common fields
        if (signUpRequest.name.isBlank() || signUpRequest.email.isBlank() || signUpRequest.password.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Name, email, and password cannot be blank")
            return@post
        }

        // Check if email exists
        if (isEmailExists(signUpRequest.email)) {
            call.respond(HttpStatusCode.Conflict, "A user with this email already exists")
            return@post
        }

        // Role-specific validation
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

        // Perform Database Insertion with transaction
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
        val role = decoded.getClaim("role").asString()?.let { UserRole.valueOf(it) }
            ?: throw IllegalArgumentException("role is missing")
        val newAccessToken = generateAccessToken(userId, role)
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
    if (isFieldExists(Students.regNo, regNo)) {
        throw ConflictException("A student with this registration number already exists")
    }
}

suspend fun validateLecturerFields(employeeRoleNo: String) {
    if (employeeRoleNo.isBlank()) {
        throw BadRequestException("Employee role number cannot be blank")
    }
    if (isFieldExists(Staff.employeeId, employeeRoleNo)) {
        throw ConflictException("A lecturer with this employee role number already exists")
    }
}

suspend fun validateAdminFields() {
    // Example: Add any constraints here, like ensuring only other admins can create admins.

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

// Updated request classes
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
    val regNo: String,
    override val role: UserRole = UserRole.STUDENT
) : SignUpRequest()

@Serializable
data class LecturerSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val employeeId: String,
    override val role: UserRole = UserRole.LECTURER
) : SignUpRequest()

@Serializable
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
