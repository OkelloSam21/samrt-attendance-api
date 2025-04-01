package com.smartattendance.android.domain.repository

import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.model.UserData
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(request: Any): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun logout()
    val isLoggedIn: Flow<Boolean>
    val currentUser: Flow<UserData?>
}
