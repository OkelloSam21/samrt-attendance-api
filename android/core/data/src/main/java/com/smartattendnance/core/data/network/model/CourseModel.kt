package com.smartattendnance.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CourseRequest(
    val name: String
)

@Serializable
data class CourseResponse(
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: String
)

@Serializable
data class CourseWithAttendance(
    val course: CourseResponse,
    val attendanceCount: Int,
    val totalSessions: Int
)

@Serializable
data class CourseUpdateRequest(
    val name: String
)