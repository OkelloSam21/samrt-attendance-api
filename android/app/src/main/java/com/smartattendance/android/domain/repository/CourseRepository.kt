package com.smartattendance.android.domain.repository

import com.smartattendance.android.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    /**
     * Get all courses from the system
     */
    suspend fun getAllCourses(): Flow<List<Course>>

    /**
     * Get a specific course by ID
     */
    suspend fun getCourseById(courseId: String): Flow<Course?>

    /**
     * Get courses taught by a specific lecturer
     */
    fun getCoursesByLecturerId(lecturerId: String): Flow<List<Course>>

    /**
     * Create a new course
     */
    suspend fun createCourse(name: String): Result<Course>

    /**
     * Update a course
     */
    suspend fun updateCourse(courseId: String, name: String): Result<Course>

    /**
     * Update a course with lecturer information
     */
    suspend fun updateCourse(
        courseId: String,
        name: String,
        lecturerId: String,
        lecturerName: String
    ): Result<Course>

    /**
     * Assign a lecturer to a course
     */
    suspend fun assignLecturerToCourse(courseId: String, lecturerId: String): Result<Course>

    /**
     * Delete a course
     */
    suspend fun deleteCourse(courseId: String): Result<Unit>

    /**
     * Seed the database with a batch of courses
     */
    suspend fun seedCourses(courses: List<String>): Result<Int>
}