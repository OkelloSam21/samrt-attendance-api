package com.smartattendance.android.data.repository

import android.util.Log
import com.smartattendance.android.data.database.dao.UserDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.LoginRequest
import com.smartattendance.android.data.network.model.LoginResponse
import com.smartattendance.android.data.network.model.RefreshTokenRequest
import com.smartattendance.android.data.network.model.SignUpResponse
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.model.UserData
import com.smartattendance.android.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val userDao: UserDao,
    private val userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
): AuthRepository {
    override suspend fun login(email: String, password: String): AuthResult {
        return when (val response = apiClient.login(LoginRequest(email, password))) {
            is ApiResponse.Success -> {
                saveTokens(response.data)
                Log.d("AuthRepositoryImpl", "Login successful userId = ${response.data.data.userId}, role = ${response.data.data.role}")
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                Log.e("AuthRepositoryImpl", "Login error: ${response.errorMessage}")
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    override suspend fun register( request: Any): AuthResult {
        return when (val response = apiClient.registerUser(request)) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                Log.e("AuthRepositoryImpl", "Registration error: ${response.errorMessage}")
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    private suspend fun processRegistration(request: Any): AuthResult {
        return when (val response = apiClient.registerUser(request)) {
            is ApiResponse.Success -> {
                saveAuthData(response.data)
                AuthResult.Success
            }
            is ApiResponse.Error -> {
                Log.e("AuthRepositoryImpl", "Registration error: ${response.errorMessage}")
                AuthResult.Error(response.errorMessage)
            }
        }
    }

    override suspend fun refreshToken(): AuthResult {
        val refreshToken = userPreferencesRepositoryImpl.refreshToken.firstOrNull() ?: return AuthResult.Error("No refresh token found")

        return when (val response = apiClient.refreshToken(RefreshTokenRequest(refreshToken))) {
            is ApiResponse.Success -> {
                saveTokens(response.data)
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

    private suspend fun saveAuthData(authResponse: SignUpResponse) {
        userPreferencesRepositoryImpl.saveAccessToken(authResponse.data.accessToken)
        userPreferencesRepositoryImpl.saveRefreshToken(authResponse.data.refreshToken)
    }

    private suspend fun saveTokens(response: LoginResponse) {
        userPreferencesRepositoryImpl.saveAccessToken(response.data.accessToken)
        userPreferencesRepositoryImpl.saveRefreshToken(response.data.refreshToken)
        userPreferencesRepositoryImpl.saveUserRole(response.data.role)
        userPreferencesRepositoryImpl.saveUserId(response.data.userId)
        userPreferencesRepositoryImpl.saveUserEmail(response.data.email)
        userPreferencesRepositoryImpl.saveUserName(response.data.name)
    }
}