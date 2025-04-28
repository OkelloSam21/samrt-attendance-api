package com.smartattendance.android.feature.student.model

import com.smartattendance.android.domain.model.Attendance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AttendanceHistoryItem(
    val id: String,
    val courseId: String,
    val courseName: String,
    val date: String,
    val time: String,
    val status: String,
    val rawDate: Date
)

fun Attendance.toHistoryItem(courseName: String = "Unknown Course"): AttendanceHistoryItem {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    return AttendanceHistoryItem(
        id = id,
        courseId = courseId,
        courseName = courseName,
        date = dateFormat.format(timestamp),
        time = timeFormat.format(timestamp),
        status = status,
        rawDate = timestamp
    )
}