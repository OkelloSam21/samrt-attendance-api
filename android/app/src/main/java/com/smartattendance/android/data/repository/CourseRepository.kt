package com.smartattendance.android.data.repository

import com.smartattendance.android.data.database.CourseEntity
import com.smartattendance.android.data.database.dao.CourseDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.CourseRequest
import com.smartattendance.android.data.network.model.CourseResponse
import com.smartattendance.android.data.network.model.CourseUpdateRequest
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.Course
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CourseRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val courseDao: CourseDao
) {
    // Get all courses (from local database, with network refresh)
    suspend fun getAllCourses(): Flow<List<Course>> {
        refreshCourses()
        return courseDao.getAllCourses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    // Get course by ID
    suspend fun getCourseById(courseId: String): Flow<Course?> {
        refreshCourse(courseId)
        return courseDao.getCourseById(courseId).map { entity ->
            entity?.toDomainModel()
        }
    }

    // Get courses by lecturer ID
    fun getCoursesByLecturerId(lecturerId: String): Flow<List<Course>> {
        return courseDao.getCoursesByLecturerId(lecturerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    // Create a new course
    suspend fun createCourse(name: String): Result<Course> {
        val request = CourseRequest(name)

        return when (val response = apiClient.createCourse(request)) {
            is ApiResponse.Success -> {
                val courseEntity = response.data.toEntity()
                courseDao.insertCourse(courseEntity)
                Result.success(courseEntity.toDomainModel())
            }

            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Update a course
    suspend fun updateCourse(courseId: String, name: String): Result<Course> {
        val request = CourseUpdateRequest(name)

        return when (val response = apiClient.updateCourse(courseId, request)) {
            is ApiResponse.Success -> {
                val courseEntity = response.data.toEntity()
                courseDao.updateCourse(courseEntity)
                Result.success(courseEntity.toDomainModel())
            }

            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Delete a course
    suspend fun deleteCourse(courseId: String): Result<Unit> {
        return when (val response = apiClient.deleteCourse(courseId)) {
            is ApiResponse.Success -> {
                courseDao.deleteCourse(courseId)
                Result.success(Unit)
            }

            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Refresh course data from network
    private suspend fun refreshCourse(courseId: String) {
        when (val response = apiClient.getCourseById(courseId)) {
            is ApiResponse.Success -> {
                courseDao.insertCourse(response.data.toEntity())
            }

            is ApiResponse.Error -> {
                // If error, we just use cached data
            }
        }
    }

    // Refresh all courses from network
    private suspend fun refreshCourses() {
        when (val response = apiClient.getAllCourses()) {
            is ApiResponse.Success -> {
                val courses = response.data.map { it.toEntity() }
                courseDao.insertCourses(courses)
            }

            is ApiResponse.Error -> {
                // If error, we just use cached data
            }
        }
    }

    // Extension function to convert database entity to domain model
    private fun CourseEntity.toDomainModel(): Course {
        return Course(
            id = id,
            name = name,
            lecturerId = lecturerId,
            lecturerName = lecturerName,
            createdAt = createdAt
        )
    }

    // Extension function to convert network response to database entity
    private fun CourseResponse.toEntity(): CourseEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return CourseEntity(
            id = id,
            name = name,
            lecturerId = lecturerId,
            lecturerName = lecturerName,
            createdAt = try {
                dateFormat.parse(createdAt) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }
}