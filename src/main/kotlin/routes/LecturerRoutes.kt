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
import java.util.*

// Data Models
@Serializable
data class LecturerCourseResponse(
    val courseId: String,
    val courseName: String,
    val totalSessions: Int,
    val scheduledSessions: List<CourseScheduleResponse>
)

@Serializable
data class CourseScheduleResponse(
    val id: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val roomNumber: String?,
    val meetingLink: String?
)

@Serializable
data class LecturerSessionResponse(
    val lectureId: String,
    val courseId: String,
    val courseName: String,
    val topic: String,
    val date: String,
    val duration: Int
)

// Route Function
fun Route.lecturerRoutes() {
    route("/lecturers") {
        authenticate("auth-jwt") {
            // Get all courses for a lecturer
            get("/courses") {
                val lecturerId = call.authorizeUser(SECRET, UserRole.LECTURER) 
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val courses = transaction {
                    (Courses innerJoin Users)
                        .select { Courses.lecturerId eq UUID.fromString(lecturerId) }
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

                            // Count total sessions for the course
                            val totalSessions = Lectures
                                .select { Lectures.courseId eq courseRow[Courses.id] }
                                .count()

                            LecturerCourseResponse(
                                courseId = courseRow[Courses.id].value.toString(),
                                courseName = courseRow[Courses.name],
                                totalSessions = totalSessions.toInt(),
                                scheduledSessions = schedules
                            )
                        }

                    courses
                }

                call.respond(HttpStatusCode.OK, courses)
            }

            // Get upcoming lectures for a lecturer
            get("/lectures") {
                val lecturerId = call.authorizeUser(SECRET, UserRole.LECTURER) 
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val upcomingLectures = transaction {
                    (Lectures innerJoin Courses)
                        .select { 
                            (Lectures.lecturerId eq UUID.fromString(lecturerId)) and 
                            (Lectures.date greaterEq Instant.now())
                        }
                        .orderBy(Lectures.date)
                        .map { lectureRow ->
                            LecturerSessionResponse(
                                lectureId = lectureRow[Lectures.id].value.toString(),
                                courseId = lectureRow[Lectures.courseId].value.toString(),
                                courseName = lectureRow[Courses.name],
                                topic = lectureRow[Lectures.topic],
                                date = lectureRow[Lectures.date].toString(),
                                duration = lectureRow[Lectures.duration]
                            )
                        }
                }

                call.respond(HttpStatusCode.OK, upcomingLectures)
            }
        }
    }
}