package features.auth.models

import domain.models.UserRole
import kotlinx.serialization.Serializable

/**
 * Base signup request interface
 */
@Serializable
sealed class SignUpRequest {
    abstract val name: String
    abstract val email: String
    abstract val password: String
    abstract val role: UserRole
}

/**
 * Student-specific signup request
 */
@Serializable
data class StudentSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val regNo: String,
    val department: String? = null,
    val yearOfStudy: Int? = null,
    override val role: UserRole = UserRole.STUDENT
) : SignUpRequest()

/**
 * Lecturer-specific signup request
 */
@Serializable
data class LecturerSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val employeeId: String,
    val department: String? = null,
    override val role: UserRole = UserRole.LECTURER
) : SignUpRequest()

/**
 * Admin-specific signup request
 */
@Serializable
data class AdminSignUpRequest(
    override val name: String,
    override val email: String,
    override val password: String,
    val department: String? = null,
    override val role: UserRole = UserRole.ADMIN
) : SignUpRequest()

/**
 * Login request
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Refresh token request
 */
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * Authentication response
 */
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val email: String,
    val role: String
)