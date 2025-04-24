package com.smartattendance.android.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Login Request
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// Registration requests for different user types
/**
 * Base request DTO that will be used for all sign-up requests
 * This matches the SignUpRequestDTO on the server
 */
@Serializable
data class SignUpRequestDTO(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val department: String = "",
    val regNo: String? = null,
    val employeeId: String? = null,
    val yearOfStudy: Int? = null
)

/**
 * Student-specific request for client-side type safety
 */
@Serializable
data class StudentSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val regNo: String,
    val department: String = "",
    val yearOfStudy: Int? = null,
    val role: String = "STUDENT"
)

/**
 * Lecturer-specific request for client-side type safety
 */
@Serializable
data class LecturerSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val employeeId: String,
    val department: String = "",
    val role: String = "LECTURER"
)

/**
 * Admin-specific request for client-side type safety
 */
@Serializable
data class AdminSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val department: String = "",
    val role: String = "ADMIN"
)

// Authentication response
@Serializable
data class AuthResponseWrapper(
   val signUpResponse: SignUpResponse,
    val loginResponse: LoginResponse
)

@Serializable
data class SignUpResponse(
    val success: Boolean,
    val data: Data
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val data: Data,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
@SerialName("data")
data class Data(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val regNo: String? = null,
    val employeeRoleNo: String? = null,
    val createdAt: String,
    val updatedAt: String
)