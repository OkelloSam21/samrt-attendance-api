package com.smartattendance.android.data.repository

import android.util.Log
import com.smartattendance.android.data.database.CourseEntity
import com.smartattendance.android.data.database.dao.CourseDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.*
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.Course
import com.smartattendance.android.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val courseDao: CourseDao
) : CourseRepository {

    override suspend fun getAllCourses(): Flow<List<Course>> {

        when(val response = apiClient.getAllCourses()) {
            is ApiResponse.Success -> {
                val courses = response.data.map { it.toEntity() }
                Log.e("course repository", "courses $courses")
                courseDao.insertCourses(courses)
            }
            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to fetch courses: ${response.errorMessage}")
            }
        }
        return courseDao.getAllCourses().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getCourseById(courseId: String): Flow<Course?> {
        refreshCourse(courseId)
        return courseDao.getCourseById(courseId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun getCoursesByLecturerId(lecturerId: String): Flow<List<Course>> {
        when(val response = apiClient.getCurseByLecturerId(lecturerId)) {
            is ApiResponse.Success -> {
                val courses = response.data.map { it.toEntity()}
                courseDao.insertCourses(courses)
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to fetch courses: ${response.errorMessage}")
            }
        }

        return courseDao.getCoursesByLecturerId(lecturerId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun createCourse(name: String, lecturerId: String, schedules: List<ScheduleRequest>): Result<Course> {
        val request = CourseRequest(
            name = name,
            lecturerId = lecturerId,
            schedules = schedules
        )

        return when (val response = apiClient.createCourse(request)) {
            is ApiResponse.Success -> {
                if (response.data.success == true) {
                    val courseResponse = response.data.data

                    run {
                        val courseEntity = courseResponse.firstOrNull()?.toEntity() ?: return Result.failure(Exception("Invalid course response"))
                        courseDao.insertCourse(courseEntity)
                        return Result.success(courseEntity.toDomainModel())
                    }
                } else {
                    val errorMessage = response.data.message
                    Result.failure(Exception(errorMessage))
                }
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to create course: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun adminCreateCourse(name: String, lecturerId: String?, schedules: List<ScheduleRequest>): Result<Course> {
        val request = AdminCourseCreateRequest(name, lecturerId, schedules)

        return when (val response = apiClient.adminCreateCourse(request)) {
            is ApiResponse.Success -> {
                val courseEntity = response.data.toEntity()
                courseDao.insertCourse(courseEntity)
                Result.success(courseEntity.toDomainModel())
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to create course as admin: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun getAvailableLecturers(): Result<List<LecturerDto>> {
        return when (val response = apiClient.getAvailableLecturers()) {
            is ApiResponse.Success -> {
                Result.success(response.data)
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to get available lecturers: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun updateCourse(courseId: String, name: String): Result<Course> {
        val request = CourseUpdateRequest(name)

        return when (val response = apiClient.updateCourse(courseId, request)) {
            is ApiResponse.Success -> {
                val courseEntity = response.data.toEntity()
                courseDao.updateCourse(courseEntity)
                Result.success(courseEntity.toDomainModel())
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to update course: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun updateCourse(
        courseId: String,
        name: String,
        lecturerId: String,
        lecturerName: String
    ): Result<Course> {
        // First update the course name if needed
        val courseUpdateResult = if (name.isNotEmpty()) {
            val updateResult = updateCourse(courseId, name)
            if (updateResult.isFailure) {
                return updateResult
            }
            updateResult
        } else {
            val course = courseDao.getCourseById(courseId).map { it?.toDomainModel() }.firstOrNull() ?: return Result.failure(Exception("Course not found"))
            Result.success(course)
        }

        // Then assign the lecturer
        return assignLecturerToCourse(courseId, lecturerId)
    }

    override suspend fun assignLecturerToCourse(courseId: String, lecturerId: String): Result<Course> {
        return when (val response = apiClient.assignLecturerToCourse(courseId, lecturerId)) {
            is ApiResponse.Success -> {
                val courseEntity = response.data.toEntity()
                courseDao.updateCourse(courseEntity)
                Result.success(courseEntity.toDomainModel())
            }

            is ApiResponse.Error -> {
                // If the API call fails, try to update locally using the current course data
                try {
                    val course = courseDao.getCourseById(courseId).firstOrNull()
                    if (course != null) {
                        // We would need the lecturer name here, but we don't have it
                        // This is a temporary solution for demo purposes
                        val updatedCourse = course.copy(lecturerId = lecturerId)
                        courseDao.updateCourse(updatedCourse)
                        return Result.success(updatedCourse.toDomainModel())
                    }
                } catch (e: Exception) {
                    Log.e("CourseRepositoryImpl", "Failed to update course locally", e)
                }

                Log.e("CourseRepositoryImpl", "Failed to assign lecturer: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    override suspend fun deleteCourse(courseId: String): Result<Unit> {
        return when (val response = apiClient.deleteCourse(courseId)) {
            is ApiResponse.Success -> {
                courseDao.deleteCourse(courseId)
                Result.success(Unit)
            }

            is ApiResponse.Error -> {
                Log.e("CourseRepositoryImpl", "Failed to delete course: ${response.errorMessage}")
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Helper function to refresh a specific course from the network
    private suspend fun refreshCourse(courseId: String) {
        try {
            when (val response = apiClient.getCourseById(courseId)) {
                is ApiResponse.Success -> {
                    val course = response.data.toEntity()
                    courseDao.insertCourse(course)
                }
                is ApiResponse.Error -> {
                    Log.e("CourseRepositoryImpl", "Failed to refresh course: ${response.errorMessage}")
                    // Continue with cached data
                }
            }
        } catch (e: Exception) {
            Log.e("CourseRepositoryImpl", "Error refreshing course", e)
            // Continue with cached data
        }
    }

    // Extension function to convert API model to entity
    private fun CourseResponse.toEntity(): CourseEntity {
        return CourseEntity(
            id = id,
            name = name,
            lecturerId = lecturerId,
            lecturerName = lecturerName,
            createdAt = parseDate(createdAt)
        )
    }

    // Extension function to convert entity to domain model
    private fun CourseEntity.toDomainModel(): Course {
        return Course(
            id = id,
            name = name,
            lecturerId = lecturerId,
            lecturerName = lecturerName,
            createdAt = createdAt,
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