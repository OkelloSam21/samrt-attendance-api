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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
        latitude: Double? ,
        longitude: Double? ,
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
    override suspend fun getQrCodeForLatestSession(): Result<String> {
        return when (val response = apiClient.getQrCodeForSession()) {
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
        latitude: Double? ,
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
            geo_fence = GeoFence(
                latitude = latitude ?: 0.0,
                longitude = longitude ?: 0.0,
                radius_meters = radiusMeters ?: 0
            ),
            createdAt = createdAt.toString(),
            expiresAt = expiresAt.toString(),
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