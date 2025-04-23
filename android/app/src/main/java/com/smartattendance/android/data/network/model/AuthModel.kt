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
@Serializable
data class StudentSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "STUDENT",
    val regNo: String
)

@Serializable
data class LecturerSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "LECTURER",
    val employeeRoleNo: String
)

@Serializable
data class AdminSignUpRequest(
    val name: String,
    val email: String,
    val password: String,
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
    val message: String? = null,
    val userId: String
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