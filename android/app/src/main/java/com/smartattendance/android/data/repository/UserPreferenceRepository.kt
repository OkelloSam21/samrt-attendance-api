package com.smartattendance.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): UserPreferencesRepository {
    private val dataStore = context.dataStore

    // Keys for storing preferences
    private object PreferenceKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
    }

    // Access token
    override val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.ACCESS_TOKEN]
    }

    override suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ACCESS_TOKEN] = token
        }
    }

    // Refresh token
    override val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REFRESH_TOKEN]
    }

    override suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.REFRESH_TOKEN] = token
        }
    }

    // User ID
    override val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ID]
    }

    override suspend fun saveUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = id
        }
    }

    // User Role
    override val userRole: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_ROLE]
    }

    override suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ROLE] = role
        }
    }

    // User Email
    override val userEmail: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_EMAIL]
    }

    override suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_EMAIL] = email
        }
    }

    // User Name
    override val userName: Flow<String?> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_NAME]
    }

    override suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_NAME] = name
        }
    }

    // Clear all user data (for logout)
    override suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.ACCESS_TOKEN)
            preferences.remove(PreferenceKeys.REFRESH_TOKEN)
            preferences.remove(PreferenceKeys.USER_ID)
            preferences.remove(PreferenceKeys.USER_ROLE)
            preferences.remove(PreferenceKeys.USER_EMAIL)
            preferences.remove(PreferenceKeys.USER_NAME)
        }
    }
}