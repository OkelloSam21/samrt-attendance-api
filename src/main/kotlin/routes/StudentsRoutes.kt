package routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import models.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import common.util.SECRET
import common.util.authorizeUser

// Data Models
@kotlinx.serialization.Serializable
data class StudentCourseResponse(
    val courseId: String,
    val courseName: String,
    val lecturerName: String,
    val schedules: List<CourseScheduleResponse>,
    val attendancePercentage: Double
)

@Serializable
data class StudentAttendanceResponse(
    val courseId: String,
    val courseName: String,
    val totalSessions: Int,
    val attendedSessions: Int,
    val attendancePercentage: Double
)

// Route Function
fun Route.studentRoutes() {
    route("/students") {
        authenticate("auth-jwt") {
            // Get courses for a student
            get("/courses") {
                val studentId = call.authorizeUser(SECRET, UserRole.STUDENT)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val courses = transaction {
                    // You'll need to create a StudentCourses table to track course enrollment
                    // For now, this is a placeholder implementation
                    (Courses innerJoin Users)
                        .select { Courses.lecturerId eq Users.id }
                        .map { courseRow ->
                            // Fetch course schedules
                            val schedules = CourseSchedules
                                .select { CourseSchedules.courseId eq courseRow[Courses.id] }
                                .map { scheduleRow ->
                                    CourseScheduleResponse(
                                        id = scheduleRow[CourseSchedules.id].value.toString(),
                                        dayOfWeek = scheduleRow[CourseSchedules.dayOfWeek],
                                        startTime = scheduleRow[CourseSchedules.startTime].toString(),
                                        endTime = scheduleRow[CourseSchedules.endTime].toString(),
                                        roomNumber = scheduleRow[CourseSchedules.roomNumber],
                                        meetingLink = scheduleRow[CourseSchedules.meetingLink]
                                    )
                                }

                            // Calculate attendance percentage (placeholder logic)
                            val totalSessions = Lectures
                                .select { Lectures.courseId eq courseRow[Courses.id] }
                                .count()

                            val attendedSessions = 0 // Placeholder - implement actual attendance tracking

                            StudentCourseResponse(
                                courseId = courseRow[Courses.id].value.toString(),
                                courseName = courseRow[Courses.name],
                                lecturerName = courseRow[Users.name],
                                schedules = schedules,
                                attendancePercentage = if (totalSessions > 0) {
                                    (attendedSessions.toDouble() / totalSessions) * 100
                                } else 0.0
                            )
                        }
                }

                call.respond(HttpStatusCode.OK, courses)
            }

            // Get student's attendance details
            get("/attendance") {
                val studentId = call.authorizeUser(SECRET, UserRole.STUDENT)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val attendanceDetails = transaction {
                    (Courses innerJoin Lectures)
                        .select { Courses.id eq Lectures.courseId }
                        .map { courseRow ->
                            // Placeholder for attendance calculation
                            val totalSessions = Lectures
                                .select { Lectures.courseId eq courseRow[Courses.id] }
                                .count()

                            val attendedSessions = 0 // Placeholder - implement actual attendance tracking

                            StudentAttendanceResponse(
                                courseId = courseRow[Courses.id].value.toString(),
                                courseName = courseRow[Courses.name],
                                totalSessions = totalSessions.toInt(),
                                attendedSessions = attendedSessions,
                                attendancePercentage = if (totalSessions > 0) {
                                    (attendedSessions.toDouble() / totalSessions) * 100
                                } else 0.0
                            )
                        }
                }

                call.respond(HttpStatusCode.OK, attendanceDetails)
            }
        }
    }
}