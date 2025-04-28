package com.smartattendance.android.domain.repository

import com.smartattendance.android.data.network.model.AttendanceSession
import com.smartattendance.android.data.network.model.MarkAttendanceResponse
import com.smartattendance.android.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun createAttendanceSession(
        courseId: String,
        durationMinutes: Int,
        sessionType: String,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusMeters: Int? = null
    ): Result<AttendanceSession>

    suspend fun getQrCodeForLatestSession(sessionId: String): Result<String>

    suspend fun markAttendance(
        sessionCode: String,
        latitude: Double? = null,
        longitude: Double? = null,
        deviceId: String? = Math.random().toString()
    ): Result<MarkAttendanceResponse>

    fun getActiveAttendanceSessions(): Flow<List<AttendanceSession>>
    fun getAttendanceSessionsByLecturerId(lecturerId: String): Flow<List<AttendanceSession>>
    fun getAttendanceSessionsByCourseId(courseId: String): Flow<List<AttendanceSession>>
    fun getAttendancesByStudentId(studentId: String): Flow<List<Attendance>>
    fun getAttendancesBySessionId(sessionId: String): Flow<List<Attendance>>
    fun getAttendanceCountForStudentInCourse(studentId: String, courseId: String): Flow<Int>

    // New methods for lecturer features
    fun getSessionById(sessionId: String): Flow<AttendanceSession?>
    suspend fun endSession(sessionId: String): Result<Unit>
    suspend fun generateQrCode(sessionId: String): Result<String>
    fun getSessionsByLecturerAndStatus(lecturerId: String, isActive: Boolean): Flow<List<AttendanceSession>>
    fun getAttendanceStatisticsBySession(sessionId: String): Flow<AttendanceStats>
}

data class AttendanceStats(
    val totalStudents: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int
)