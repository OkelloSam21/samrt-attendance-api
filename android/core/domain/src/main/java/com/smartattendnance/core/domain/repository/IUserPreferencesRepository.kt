package com.smartattendnance.core.domain.repository

import com.smartattendnance.core.domain.model.Attendance
import com.smartattendnance.core.domain.model.AttendanceSession
import com.smartattendnance.core.domain.model.AuthResult
import com.smartattendnance.core.domain.model.Course
import com.smartattendnance.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface IUserPreferencesRepository {
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

interface IAuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun registerStudent(name: String, email: String, password: String, regNo: String): AuthResult
    suspend fun registerLecturer(name: String, email: String, password: String, employeeRoleNo: String): AuthResult
    suspend fun registerAdmin(name: String, email: String, password: String): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun logout()
    val isLoggedIn: Flow<Boolean>
    val currentUser: Flow<UserData?>
}

interface ICourseRepository {
    fun getAllCourses(): Flow<List<Course>>
    fun getCourseById(courseId: String): Flow<Course?>
    fun getCoursesByLecturerId(lecturerId: String): Flow<List<Course>>
    suspend fun createCourse(name: String): Result<Course>
    suspend fun updateCourse(courseId: String, name: String): Result<Course>
    suspend fun deleteCourse(courseId: String): Result<Unit>
}

interface IAttendanceRepository {
    suspend fun createAttendanceSession(
        courseId: String,
        durationMinutes: Int,
        sessionType: String,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusMeters: Int? = null
    ): Result<AttendanceSession>
    
    suspend fun getQrCodeForLatestSession(): Result<String>
    
    suspend fun markAttendance(
        sessionCode: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<Attendance>
    
    fun getActiveAttendanceSessions(): Flow<List<AttendanceSession>>
    fun getAttendanceSessionsByLecturerId(lecturerId: String): Flow<List<AttendanceSession>>
    fun getAttendanceSessionsByCourseId(courseId: String): Flow<List<AttendanceSession>>
    fun getAttendancesByStudentId(studentId: String): Flow<List<Attendance>>
    fun getAttendancesBySessionId(sessionId: String): Flow<List<Attendance>>
    fun getAttendanceCountForStudentInCourse(studentId: String, courseId: String): Flow<Int>
}