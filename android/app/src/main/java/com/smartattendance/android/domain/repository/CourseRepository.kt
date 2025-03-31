package com.smartattendance.android.domain.repository

import com.smartattendance.android.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    suspend fun getAllCourses(): Flow<List<Course>>
    suspend fun getCourseById(courseId: String): Flow<Course?>
    fun getCoursesByLecturerId(lecturerId: String): Flow<List<Course>>
    suspend fun createCourse(name: String): Result<Course>
    suspend fun updateCourse(courseId: String, name: String): Result<Course>
    suspend fun deleteCourse(courseId: String): Result<Unit>
}