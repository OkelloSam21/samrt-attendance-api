package com.smartattendnance.core.data.repository

import com.smartattendnance.core.data.database.UserEntity
import com.smartattendnance.core.data.database.dao.UserDao
import com.smartattendnance.core.data.network.ApiClient
import com.smartattendnance.core.data.network.model.AdminSignUpRequest
import com.smartattendnance.core.data.network.model.AuthResponse
import com.smartattendnance.core.data.network.model.LecturerSignUpRequest
import com.smartattendnance.core.data.network.model.LoginRequest
import com.smartattendnance.core.data.network.model.RefreshTokenRequest
import com.smartattendnance.core.data.network.model.StudentSignUpRequest
import com.smartattendnance.core.data.network.util.ApiResponse
import com.smartattendnance.core.domain.model.AuthResult
import com.smartattendnance.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDao: UserDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend fun login(email: String, password: String): AuthResult {
        return when (val response = apiClient.login(LoginRequest(email, password))) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    suspend fun registerStudent(name: String, email: String, password: String, regNo: String): AuthResult {
        val request = StudentSignUpRequest(
            name = name,
            email = email,
            password = password,
            regNo = regNo
        )

        return when (val response = apiClient.registerStudent(request)) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    suspend fun registerLecturer(
        name: String,
        email: String,
        password: String,
        employeeRoleNo: String
    ): AuthResult {
        val request = LecturerSignUpRequest(
            name = name,
            email = email,
            password = password,
            employeeRoleNo = employeeRoleNo
        )

        return when (val response = apiClient.registerLecturer(request)) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    suspend fun registerAdmin(name: String, email: String, password: String): AuthResult {
        val request = AdminSignUpRequest(
            name = name,
            email = email,
            password = password
        )

        return when (val response = apiClient.registerAdmin(request)) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    suspend fun refreshToken(): AuthResult {
        val refreshToken = userPreferencesRepository.refreshToken.firstOrNull() ?: return AuthResult.Error("No refresh token found")

        return when (val response = apiClient.refreshToken(RefreshTokenRequest(refreshToken))) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    suspend fun logout() {
        userPreferencesRepository.clearUserData()
    }

    val isLoggedIn: Flow<Boolean> = userPreferencesRepository.accessToken.map { token ->
        !token.isNullOrEmpty()
    }

    val currentUser: Flow<UserData?> = userPreferencesRepository.userId.map { userId ->
        if (userId != null) {
            UserData(
                id = userId,
                name = userPreferencesRepository.userName.firstOrNull() ?: "",
                email = userPreferencesRepository.userEmail.firstOrNull() ?: "",
                role = userPreferencesRepository.userRole.firstOrNull() ?: ""
            )
        } else {
            null
        }
    }

    private suspend fun saveAuthData(authResponse: AuthResponse) {
        userPreferencesRepository.saveAccessToken(authResponse.accessToken)
        userPreferencesRepository.saveRefreshToken(authResponse.refreshToken)
        userPreferencesRepository.saveUserId(authResponse.user.id)
        userPreferencesRepository.saveUserRole(authResponse.user.role)
        userPreferencesRepository.saveUserEmail(authResponse.user.email)
        userPreferencesRepository.saveUserName(authResponse.user.name)

        // Save user to local database
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val userEntity = UserEntity(
            id = authResponse.user.id,
            name = authResponse.user.name,
            email = authResponse.user.email,
            role = authResponse.user.role,
            regNo = authResponse.user.regNo,
            employeeRoleNo = authResponse.user.employeeRoleNo,
            createdAt = try {
                dateFormat.parse(authResponse.user.createdAt) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            updatedAt = try {
                dateFormat.parse(authResponse.user.updatedAt) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
        userDao.insertUser(userEntity)
    }
}