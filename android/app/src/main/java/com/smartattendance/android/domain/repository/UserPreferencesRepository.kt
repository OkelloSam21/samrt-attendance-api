package com.smartattendance.android.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    val userId: Flow<String?>
    val userRole: Flow<String?>
    val userEmail: Flow<String?>
    val userName: Flow<String?>

    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun saveUserId(id: String)
    suspend fun saveUserRole(role: String)
    suspend fun saveUserEmail(email: String)
    suspend fun saveUserName(name: String)
    suspend fun clearUserData()
}
