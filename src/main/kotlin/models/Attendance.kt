package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Attendance : UUIDTable("attendance") {
    val studentId = reference("student_id", Users.id)
    val courseId = reference("course_id", Courses.id)
    val date = timestamp("date")
    val status = enumerationByName("status", 10, AttendanceStatus::class)
}

enum class AttendanceStatus { PRESENT, ABSENT, LATE }
