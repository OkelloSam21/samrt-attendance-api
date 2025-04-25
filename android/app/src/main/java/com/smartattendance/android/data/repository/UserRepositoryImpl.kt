package com.smartattendance.android.data.repository

import android.util.Log
import com.smartattendance.android.data.database.UserEntity
import com.smartattendance.android.data.database.dao.UserDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.CreateUserRequest
import com.smartattendance.android.data.network.model.UpdateProfileRequest
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.UserData
import com.smartattendance.android.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getAllUsers(): Flow<List<UserData>> {
        refreshUsers() // Try to refresh from network
        return userDao.getAllUsers().map { users ->
            users.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserById(userId: String): Flow<UserData?> {
        refreshUser(userId) // Try to refresh from network
        return userDao.getUserById(userId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getUsersByRole(role: String): Flow<List<UserData>> {
        return userDao.getUsersByRole(role).map { users ->
            users.map { it.toDomainModel() }
        }
    }

    override fun getLecturers(): Flow<List<UserData>> {
        return getUsersByRole("LECTURER")
    }

    override fun getStudents(): Flow<List<UserData>> {
        return getUsersByRole("STUDENT")
    }

    override suspend fun createUser(userData: UserData): Result<UserData> {
        val request = CreateUserRequest(
            name = userData.name,
            email = userData.email,
            password = "defaultPassword123", // In a real app, this would be handled differently
            role = userData.role
        )

        return when (val response = apiClient.createUser(request)) {
            is ApiResponse.Success -> {
                val userEntity = UserEntity(
                    id = response.data.id,
                    name = userData.name,
                    email = userData.email,
                    role = userData.role,
                    regNo = userData.regNo,
                    employeeRoleNo = userData.employeeRoleNo,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                userDao.insertUser(userEntity)
                Result.success(userEntity.toDomainModel())
            }
            is ApiResponse.Error -> {
                Log.e("UserRepositoryImpl", "Failed to create user: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun updateUser(userData: UserData): Result<UserData> {
        val request = UpdateProfileRequest(
            name = userData.name,
            email = userData.email,
            regNo = userData.regNo,
            employeeRoleNo = userData.employeeRoleNo
        )

        return when (val response = apiClient.updateUser(userData.id, request)) {
            is ApiResponse.Success -> {
                val userEntity = UserEntity(
                    id = userData.id,
                    name = userData.name,
                    email = userData.email,
                    role = userData.role,
                    regNo = userData.regNo,
                    employeeRoleNo = userData.employeeRoleNo,
                    createdAt = Date(), // Ideally this would come from the API
                    updatedAt = Date()
                )
                userDao.updateUser(userEntity)
                Result.success(userEntity.toDomainModel())
            }
            is ApiResponse.Error -> {
                Log.e("UserRepositoryImpl", "Failed to update user: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return when (val response = apiClient.deleteUser(userId)) {
            is ApiResponse.Success -> {
                userDao.deleteUser(userId)
                Result.success(Unit)
            }
            is ApiResponse.Error -> {
                Log.e("UserRepositoryImpl", "Failed to delete user: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Helper function to refresh users from the network
    private suspend fun refreshUsers() {
        try {
            when (val response = apiClient.getAllUsers()) {
                is ApiResponse.Success -> {
                    val users = response.data.map { it.toEntity() }
                    userDao.insertUsers(users)
                }
                is ApiResponse.Error -> {
                    Log.e("UserRepositoryImpl", "Failed to refresh users: ${response.errorMessage}")
                    // Continue with cached data
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error refreshing users", e)
            // Continue with cached data
        }
    }

    // Helper function to refresh a specific user from the network
    private suspend fun refreshUser(userId: String) {
        try {
            when (val response = apiClient.getUserById(userId)) {
                is ApiResponse.Success -> {
                    val user = response.data.toEntity()
                    userDao.insertUser(user)
                }
                is ApiResponse.Error -> {
                    Log.e("UserRepositoryImpl", "Failed to refresh user: ${response.errorMessage}")
                    // Continue with cached data
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepositoryImpl", "Error refreshing user", e)
            // Continue with cached data
        }
    }

    // Extension function to convert API model to entity
    private fun com.smartattendance.android.data.network.model.UserProfileResponse.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            role = role,
            regNo = regNo,
            employeeRoleNo = employeeRoleNo,
            createdAt = parseDate(createdAt),
            updatedAt = parseDate(updatedAt)
        )
    }

    // Extension function to convert entity to domain model
    private fun UserEntity.toDomainModel(): UserData {
        return UserData(
            id = id,
            name = name,
            email = email,
            role = role,
            regNo = regNo,
            employeeRoleNo = employeeRoleNo
        )
    }

    // Helper function to parse dates from strings
    private fun parseDate(dateString: String): Date {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
    }
}