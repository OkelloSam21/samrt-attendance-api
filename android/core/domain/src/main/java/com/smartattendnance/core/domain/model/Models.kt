package com.smartattendnance.core.domain.model

import java.util.Date

// User-related domain models
data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val regNo: String? = null,
    val employeeRoleNo: String? = null
)

enum class AuthResult {
    Success,
    Error(val message: String) {
        override fun toString(): String = message
    }
}

// Course domain model
data class Course(
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: Date
)

// Attendance models
data class Location(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int
)

data class AttendanceSession(
    val id: String,
    val courseId: String,
    val lecturerId: String,
    val durationMinutes: Int,
    val sessionType: String,
    val sessionCode: String,
    val location: Location? = null,
    val createdAt: Date,
    val expiresAt: Date,
    val isActive: Boolean
)

data class Attendance(
    val id: String,
    val sessionId: String,
    val studentId: String,
    val courseId: String,
    val timestamp: Date,
    val status: String,
    val location: Location? = null
)

// Reports and statistics models
data class AttendanceStats(
    val totalSessions: Int,
    val attendedSessions: Int,
    val attendancePercentage: Float,
    val lastAttendance: Date?
)

data class CourseAttendanceStats(
    val courseId: String,
    val courseName: String,
    val totalSessions: Int,
    val totalStudents: Int,
    val averageAttendance: Float
)

data class StudentAttendanceReport(
    val studentId: String,
    val studentName: String,
    val courseId: String,
    val courseName: String,
    val sessions: List<Attendance>,
    val attendancePercentage: Float
)