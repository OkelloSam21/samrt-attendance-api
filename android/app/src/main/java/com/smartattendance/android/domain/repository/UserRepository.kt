package com.smartattendance.android.domain.repository

import com.smartattendance.android.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * Get all users from the system
     */
    suspend fun getAllUsers(): Flow<List<UserData>>
    
    /**
     * Get a specific user by ID
     */
    suspend fun getUserById(userId: String): Flow<UserData?>
    
    /**
     * Get users by role (e.g., "STUDENT", "LECTURER", "ADMIN")
     */
    fun getUsersByRole(role: String): Flow<List<UserData>>
    
    /**
     * Get all lecturers
     */
    fun getLecturers(): Flow<List<UserData>>
    
    /**
     * Get all students
     */
    fun getStudents(): Flow<List<UserData>>
    
    /**
     * Create a new user
     */
    suspend fun createUser(userData: UserData): Result<UserData>
    
    /**
     * Update an existing user
     */
    suspend fun updateUser(userData: UserData): Result<UserData>
    
    /**
     * Delete a user
     */
    suspend fun deleteUser(userId: String): Result<Unit>
}