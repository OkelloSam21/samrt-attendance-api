package com.smartattendance.android.domain.repository

import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun registerStudent(name: String, email: String, password: String, regNo: String): AuthResult
    suspend fun registerLecturer(name: String, email: String, password: String, employeeRoleNo: String): AuthResult
    suspend fun registerAdmin(name: String, email: String, password: String): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun logout()
    val isLoggedIn: Flow<Boolean>
    val currentUser: Flow<UserData?>
}
