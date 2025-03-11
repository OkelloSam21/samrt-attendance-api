package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

enum class SessionType { PHYSICAL, ONLINE }
enum class VerificationMethod { MANUAL, QR_CODE, GEOLOCATION, BIOMETRIC, WEBCAM, OTP }

object AttendanceSessions : UUIDTable("attendance_sessions") {
    val courseId = reference("course_id", Courses.id)
    val lecturerId = reference("lecturer_id", Users.id)
    val sessionCode = varchar("session_code", 10).uniqueIndex()
    val sessionType = enumerationByName("session_type", 10, SessionType::class)
    val createdAt = timestamp("created_at")
    val expiresAt = timestamp("expires_at")
    val locationId = uuid("location_id").nullable()
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val radiusMeters = double("radius_meters").nullable()
}

// Update your existing Attendance table to support sessions
object Attendance : UUIDTable("attendance") {
    val studentId = reference("student_id", Users.id)
    val courseId = reference("course_id", Courses.id)
    val date = timestamp("date")
    val status = enumerationByName("status", 10, AttendanceStatus::class)
    val sessionId = reference("session_id", AttendanceSessions.id).nullable()
    val verificationMethod = enumerationByName("verification_method", 20, VerificationMethod::class)
    val deviceId = varchar("device_id", 255).nullable()
    val locationLatitude = double("latitude").nullable()
    val locationLongitude = double("longitude").nullable()
}

enum class AttendanceStatus { PRESENT, ABSENT, LATE }

// For online verification if needed
object AttendanceVerifications : UUIDTable("attendance_verifications") {
    val attendanceId = reference("attendance_id", Attendance.id)
    val verificationType = varchar("verification_type", 20)
    val verificationData = text("verification_data").nullable()  // Base64 encoded data
    val verifiedAt = timestamp("verified_at")
    val success = bool("success")
}