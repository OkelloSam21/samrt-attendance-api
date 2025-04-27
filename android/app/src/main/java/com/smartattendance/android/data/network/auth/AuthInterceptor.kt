package com.smartattendance.android.data.network.auth

import android.util.Log
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.RefreshTokenRequest
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.data.repository.UserPreferencesRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepositoryImpl
)
{
    private val TAG = "AuthInterceptor"

    /**
     * Adds an authorization header to all requests
     */
    fun addAuthHeaders(builder: DefaultRequest.DefaultRequestBuilder) {
        runBlocking {
            val accessToken = userPreferencesRepository.accessToken.first()
            if (!accessToken.isNullOrEmpty()) {
                builder.header(HttpHeaders.Authorization, "Bearer $accessToken")
                Log.d(TAG, "Added auth header with token: ${accessToken.take(10)}...")
            } else {
                Log.d(TAG, "No access token available")
            }
        }
    }

    /**
     * Handle token refresh when needed
     */
    suspend fun refreshTokenIfNeeded(): Boolean {
        val refreshToken = userPreferencesRepository.refreshToken.first()
        if (refreshToken.isNullOrEmpty()) {
            Log.d(TAG, "No refresh token available")
            return false
        }

        try {
            Log.d(TAG, "Attempting to refresh token")
            val client = createTempApiClient()
            val response = client.refreshToken(RefreshTokenRequest(refreshToken))

            return when (response) {
                is ApiResponse.Success -> {
                    Log.d(TAG, "Token refresh successful")
                    userPreferencesRepository.saveAccessToken(response.data.data.accessToken)
                    userPreferencesRepository.saveRefreshToken(response.data.data.refreshToken)
                    true
                }
                is ApiResponse.Error -> {
                    Log.e(TAG, "Token refresh failed: ${response.errorMessage}")
                    userPreferencesRepository.clearUserData()
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during token refresh", e)
            userPreferencesRepository.clearUserData()
            return false
        }
    }

    private fun createTempApiClient(): ApiClient {
        // Create a minimal HTTP client just for token refresh
        val tempClient = HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                url("https://smart-attendance-api-image-production.up.railway.app/")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        return ApiClient(tempClient)
    }
}