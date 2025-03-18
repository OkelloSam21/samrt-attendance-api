package models

import com.sun.tools.javac.file.Locations
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.javatime.timestamp

object Courses : UUIDTable("courses") {
    val name = varchar("name", 255)
    val lecturerId = reference("lecturer_id", Users.id)
    val createdAt = timestamp("created_at")
}

object CourseSchedules : UUIDTable("course_schedules") {
    val courseId = reference("course_id", Courses.id)
    val dayOfWeek = integer("day_of_week") // 1-7 for Monday-Sunday
    val startTime = time("start_time")
    val endTime = time("end_time")
    val roomNumber = varchar("room_number", 50).nullable()
    val meetingLink = text("meeting_link").nullable()
}

enum class SessionStatus { SCHEDULED, ACTIVE, CLOSED, CANCELLED }