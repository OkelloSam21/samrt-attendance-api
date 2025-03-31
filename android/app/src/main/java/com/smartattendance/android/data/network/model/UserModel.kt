package com.smartattendnance.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val regNo: String? = null,
    val employeeRoleNo: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val regNo: String? = null,
    val employeeRoleNo: String? = null
)