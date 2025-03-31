package com.smartattendance.android.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.smartattendance.android.data.network.model.SessionType
import java.util.Date

@Entity(
    tableName = "attendance",
    indices = [
        Index("sessionId"),
        Index("studentId"),
        Index("courseId")
    ]
)
data class AttendanceEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val studentId: String,
    val courseId: String,
    val timestamp: Date,
    val status: String, // Present, Absent, Late
    val latitude: Double? = null,
    val longitude: Double? = null
)

@Entity(
    tableName = "attendance_sessions",
    indices = [
        Index("courseId"),
        Index("lecturerId")
    ]
)
data class AttendanceSessionEntity(
    @PrimaryKey
    val id: String,
    val courseId: String,
    val lecturerId: String,
    val durationMinutes: Int,
    val sessionType: SessionType,
    val sessionCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Int? = null,
    val createdAt: Date,
    val expiresAt: Date
)