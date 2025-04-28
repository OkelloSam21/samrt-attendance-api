package com.smartattendance.android.data.network.model

import kotlinx.serialization.Serializable


@Serializable
data class CreateCourseApiResponse(
    val success: Boolean? = null,
    val data: List<Course?> = emptyList(),
    val message: String? = null
)

@Serializable
data class CourseResponse(
    val success: Boolean? = null,
    val data: List<Course?> = emptyList(),
)

@Serializable
data class CourseRequest(
    val name: String,
    val lecturerId: String,
    val schedules: List<ScheduleRequest>
)


@Serializable
data class ScheduleRequest(
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val roomNumber: String,
    val meetingLink: String
)

@Serializable
data class Course(
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: String,
    val schedules: List<ScheduleResponse>
)

@Serializable
data class ScheduleResponse(
    val id: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val roomNumber: String,
    val meetingLink: String
)

@Serializable
data class CourseWithAttendance(
    val course: Course,
    val attendanceCount: Int,
    val totalSessions: Int
)

@Serializable
data class CourseUpdateRequest(
    val name: String
)

@Serializable
data class AdminCourseCreateRequest(
    val name: String,
    val lecturerId: String? = null,
    val schedules: List<ScheduleRequest>? = null
)

@Serializable
data class LecturerDto(
    val id: String,
    val name: String,
    val email: String
)