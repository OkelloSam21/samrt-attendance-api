package com.smartattendance.android.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SessionType {
    PHYSICAL, VIRTUAL
}

@Serializable
data class GeoFence(
    val latitude: Double,
    val longitude: Double,
    val radius_meters: Int
)

@Serializable
data class AttendanceSessionRequest(
    val course_id: String,
    val duration_minutes: Int,
    val session_type: SessionType,
    val geo_fence: GeoFence? = null
)

@Serializable
data class AttendanceSession(
    val id: String,
    val course_id: String,
    val lecturerId: String,
    val duration_minutes: Int,
    val session_type: SessionType,
    val session_code: String,
    val geo_fence: GeoFence? = null,
    val createdAt: String,
    val expiresAt: String
)

@Serializable
data class MarkAttendanceRequest(
    val sessionCode: String,
    val location: GeoLocation? = null,
    val deviceID: String? = "ertx434dugkvh"
)

@Serializable
data class MarkAttendanceResponse(
    val success: Boolean,
    val data: AttendanceResponse,
)

@Serializable
data class ErrorResponse(
    @SerialName("error") val error: ErrorDetail
)
@Serializable
data class ErrorDetail(
    val message: String,
    val code: Int
)

@Serializable
data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class AttendanceRecord(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val courseId: String,
    val timestamp: String,
    val status: String, // Present, Absent, Late
    val location: GeoLocation? = null
)

@Serializable
data class AttendanceResponse(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val courseId: String,
    val timestamp: String,
    val status: String
)

@Serializable
data class QrCodeResponse(
    val qrCodeData: String,
    val sessionCode: String,
    val expiresAt: String
)