package com.smartattendance.android.data.repository

import com.smartattendance.android.data.database.AttendanceEntity
import com.smartattendance.android.data.database.AttendanceSessionEntity
import com.smartattendance.android.data.database.dao.AttendanceDao
import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.model.AttendanceResponse
import com.smartattendance.android.data.network.model.AttendanceSession
import com.smartattendance.android.data.network.model.AttendanceSessionRequest
import com.smartattendance.android.data.network.model.GeoFence
import com.smartattendance.android.data.network.model.GeoLocation
import com.smartattendance.android.data.network.model.MarkAttendanceRequest
import com.smartattendance.android.data.network.model.SessionType
import com.smartattendance.android.data.network.util.ApiResponse
import com.smartattendance.android.domain.model.Attendance
import com.smartattendance.android.domain.model.Location
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.AttendanceStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val attendanceDao: AttendanceDao
): AttendanceRepository {
    // Create attendance session
    override suspend fun createAttendanceSession(
        courseId: String,
        durationMinutes: Int,
        sessionType: String,
        latitude: Double?,
        longitude: Double?,
        radiusMeters: Int?
    ): Result<AttendanceSession> {
        // Create geo fence if location is provided
        val geoFence = if (latitude != null && longitude != null && radiusMeters != null) {
            GeoFence(
                latitude = latitude,
                longitude = longitude,
                radius_meters = radiusMeters
            )
        } else {
            null
        }

        val sessionTypeEnum = try {
            SessionType.valueOf(sessionType.uppercase())
        } catch (e: IllegalArgumentException) {
            SessionType.PHYSICAL
        }

        val request = AttendanceSessionRequest(
            course_id = courseId,
            duration_minutes = durationMinutes,
            session_type = sessionTypeEnum,
            geo_fence = geoFence
        )

        return when (val response = apiClient.createAttendanceSession(request)) {
            is ApiResponse.Success -> {
                val sessionEntity = response.data.toSessionEntity()
                attendanceDao.insertAttendanceSession(sessionEntity)
                Result.success(sessionEntity.toDomainModel())
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Get QR code for latest session
    override suspend fun getQrCodeForLatestSession(sessionId: String): Result<String> {
        return when (val response = apiClient.getQrCodeForSession(sessionId)) {
            is ApiResponse.Success -> {
                Result.success(response.data.qrCodeData)
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Mark attendance
    override suspend fun markAttendance(
        sessionCode: String,
        latitude: Double?,
        longitude: Double?
    ): Result<Attendance> {
        // Create location if coordinates are provided
        val location = if (latitude != null && longitude != null) {
            GeoLocation(
                latitude = latitude,
                longitude = longitude
            )
        } else {
            null
        }

        val request = MarkAttendanceRequest(
            session_code = sessionCode,
            location = location
        )

        return when (val response = apiClient.markAttendance(request)) {
            is ApiResponse.Success -> {
                val attendanceEntity = response.data.toEntity()
                attendanceDao.insertAttendanceRecord(attendanceEntity)
                Result.success(attendanceEntity.toDomainModel())
            }
            is ApiResponse.Error -> {
                Result.failure(Exception(response.errorMessage))
            }
        }
    }

    // Get active attendance sessions
    override fun getActiveAttendanceSessions(): Flow<List<AttendanceSession>> {
        return attendanceDao.getActiveAttendanceSessions(Date()).map { sessions ->
            sessions.map { it.toDomainModel() }
        }
    }

    // Get attendance sessions by lecturer ID
    override fun getAttendanceSessionsByLecturerId(lecturerId: String): Flow<List<AttendanceSession>> {
        return attendanceDao.getAttendanceSessionsByLecturerId(lecturerId).map { sessions ->
            sessions.map { it.toDomainModel() }
        }
    }

    // Get attendance sessions by course ID
    override fun getAttendanceSessionsByCourseId(courseId: String): Flow<List<AttendanceSession>> {
        return attendanceDao.getAttendanceSessionsByCourseId(courseId).map { sessions ->
            sessions.map { it.toDomainModel() }
        }
    }

    // Get attendances by student ID
    override fun getAttendancesByStudentId(studentId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendancesByStudentId(studentId).map { attendances ->
            attendances.map { it.toDomainModel() }
        }
    }

    // Get attendances by session ID
    override fun getAttendancesBySessionId(sessionId: String): Flow<List<Attendance>> {
        return attendanceDao.getAttendancesBySessionId(sessionId).map { attendances ->
            attendances.map { it.toDomainModel() }
        }
    }

    // Get attendance count for student in course
    override fun getAttendanceCountForStudentInCourse(studentId: String, courseId: String): Flow<Int> {
        return attendanceDao.getAttendanceCountForStudentInCourse(studentId, courseId)
    }

    // New methods for lecturer features
    override fun getSessionById(sessionId: String): Flow<AttendanceSession?> {
        return attendanceDao.getAttendanceSessionById(sessionId).map { sessionEntity ->
            sessionEntity?.toDomainModel()
        }
    }

    override suspend fun endSession(sessionId: String): Result<Unit> {
        // In a real app, you would call an API to end the session
        // For now, we'll just update the local database
        try {
            val session = attendanceDao.getAttendanceSessionById(sessionId).firstOrNull()
            if (session != null) {
                val updatedSession = session.copy(
                    expiresAt = Date() // Set expiry to now to effectively end the session
                )
                attendanceDao.insertAttendanceSession(updatedSession)
                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Session not found"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun generateQrCode(sessionId: String): Result<String> {
        // In a real app, you would generate a QR code or get it from the API
        // For now, we'll return the session code as the QR code content
        try {
            val session = attendanceDao.getAttendanceSessionById(sessionId).firstOrNull()
            if (session != null) {
                return Result.success(session.sessionCode)
            } else {
                return Result.failure(Exception("Session not found"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getSessionsByLecturerAndStatus(lecturerId: String, isActive: Boolean): Flow<List<AttendanceSession>> {
        return attendanceDao.getAttendanceSessionsByLecturerId(lecturerId).map { sessions ->
            val currentTime = Date()
            sessions.filter { session ->
                val isSessionActive = session.expiresAt.after(currentTime)
                isSessionActive == isActive
            }.map { it.toDomainModel() }
        }
    }

    override fun getAttendanceStatisticsBySession(sessionId: String): Flow<AttendanceStats> {
        return flow {
            try {
                val attendanceRecords = attendanceDao.getAttendancesBySessionId(sessionId).firstOrNull() ?: emptyList()
                val presentCount = attendanceRecords.count { it.status == "Present" }
                val lateCount = attendanceRecords.count { it.status == "Late" }
                val absentCount = attendanceRecords.count { it.status == "Absent" }
                val totalStudents = presentCount + lateCount + absentCount

                emit(AttendanceStats(
                    totalStudents = totalStudents,
                    presentCount = presentCount,
                    absentCount = absentCount,
                    lateCount = lateCount
                ))
            } catch (e: Exception) {
                // Return empty stats on error
                emit(AttendanceStats(
                    totalStudents = 0,
                    presentCount = 0,
                    absentCount = 0,
                    lateCount = 0
                ))
            }
        }
    }

    // Extension function to convert network response to session entity
    private fun AttendanceSession.toSessionEntity(): AttendanceSessionEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return AttendanceSessionEntity(
            id = id,
            courseId = course_id,
            lecturerId = lecturerId,
            durationMinutes = duration_minutes,
            sessionType = session_type,
            sessionCode = session_code,
            latitude = geo_fence?.latitude,
            longitude = geo_fence?.longitude,
            radiusMeters = geo_fence?.radius_meters,
            createdAt = try {
                dateFormat.parse(createdAt) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            expiresAt = try {
                dateFormat.parse(expiresAt) ?: Date()
            } catch (e: Exception) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MINUTE, duration_minutes)
                cal.time
            }
        )
    }

    // Extension function to convert network response to attendance entity
    private fun AttendanceResponse.toEntity(): AttendanceEntity {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return AttendanceEntity(
            id = id,
            sessionId = sessionId,
            studentId = studentId,
            courseId = courseId,
            timestamp = try {
                dateFormat.parse(timestamp) ?: Date()
            } catch (e: Exception) {
                Date()
            },
            status = status,
            latitude = null,
            longitude = null
        )
    }

    // Extension function to convert session entity to domain model
    private fun AttendanceSessionEntity.toDomainModel(): AttendanceSession {
        return AttendanceSession(
            id = id,
            course_id = courseId,
            lecturerId = lecturerId,
            duration_minutes = durationMinutes,
            session_type = sessionType,
            session_code = sessionCode,
            geo_fence = if (latitude != null && longitude != null && radiusMeters != null) {
                GeoFence(
                    latitude = latitude,
                    longitude = longitude,
                    radius_meters = radiusMeters
                )
            } else null,
            createdAt = createdAt.toString(),
            expiresAt = expiresAt.toString()
        )
    }

    // Extension function to convert attendance entity to domain model
    private fun AttendanceEntity.toDomainModel(): Attendance {
        return Attendance(
            id = id,
            sessionId = sessionId,
            studentId = studentId,
            courseId = courseId,
            timestamp = timestamp,
            status = status,
            location = if (latitude != null && longitude != null) {
                Location(
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = 0
                )
            } else null
        )
    }
}