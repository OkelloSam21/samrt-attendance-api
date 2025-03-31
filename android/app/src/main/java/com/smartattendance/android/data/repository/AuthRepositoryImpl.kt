package com.smartattendance.android.data.repository

import com.smartattendance.android.data.database.UserEntity
import com.smartattendance.android.data.database.dao.UserDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.AdminSignUpRequest
import com.smartattendance.android.data.network.model.AuthResponse
import com.smartattendance.android.data.network.model.LecturerSignUpRequest
import com.smartattendance.android.data.network.model.LoginRequest
import com.smartattendance.android.data.network.model.RefreshTokenRequest
import com.smartattendance.android.data.network.model.StudentSignUpRequest
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.model.UserData
import com.smartattendance.android.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val userDao: UserDao,
    private val userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
): AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult {
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

    override suspend fun registerStudent(name: String, email: String, password: String, regNo: String): AuthResult {
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

    override suspend fun registerLecturer(
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

    override suspend fun registerAdmin(name: String, email: String, password: String): AuthResult {
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

    override suspend fun refreshToken(): AuthResult {
        val refreshToken = userPreferencesRepositoryImpl.refreshToken.firstOrNull() ?: return AuthResult.Error("No refresh token found")

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

    override suspend fun logout() {
        userPreferencesRepositoryImpl.clearUserData()
    }

    override val isLoggedIn: Flow<Boolean> = userPreferencesRepositoryImpl.accessToken.map { token ->
        !token.isNullOrEmpty()
    }

    override val currentUser: Flow<UserData?> = userPreferencesRepositoryImpl.userId.map { userId ->
        if (userId != null) {
            UserData(
                id = userId,
                name = userPreferencesRepositoryImpl.userName.firstOrNull() ?: "",
                email = userPreferencesRepositoryImpl.userEmail.firstOrNull() ?: "",
                role = userPreferencesRepositoryImpl.userRole.firstOrNull() ?: ""
            )
        } else {
            null
        }
    }

    private suspend fun saveAuthData(authResponse: AuthResponse) {
        userPreferencesRepositoryImpl.saveAccessToken(authResponse.accessToken)
        userPreferencesRepositoryImpl.saveRefreshToken(authResponse.refreshToken)
        userPreferencesRepositoryImpl.saveUserId(authResponse.user.id)
        userPreferencesRepositoryImpl.saveUserRole(authResponse.user.role)
        userPreferencesRepositoryImpl.saveUserEmail(authResponse.user.email)
        userPreferencesRepositoryImpl.saveUserName(authResponse.user.name)

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