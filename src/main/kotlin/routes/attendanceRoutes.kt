package routes

import auth.authorize
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun Route.attendanceRoutes() {
    route("/attendance") {
        // MARK ATTENDANCE (Lecturer only)
        post {
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@post call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@post
            }

            val request = call.receive<Map<String, Any>>()
            val courseId = request["course_id"] as? String
                ?: return@post call.respond(mapOf("error" to "Course ID is required"))
            
            // Check if attendance records are provided as a list
            @Suppress("UNCHECKED_CAST")
            val attendanceRecords = request["attendance"] as? List<Map<String, Any>>
                ?: return@post call.respond(mapOf("error" to "Attendance records are required"))

            val date = Instant.now()
            val createdRecords = mutableListOf<UUID>()

            transaction {
                for (record in attendanceRecords) {
                    val studentId = record["student_id"] as String
                    val status = AttendanceStatus.valueOf((record["status"] as String).uppercase())
                    
                    val recordId = UUID.randomUUID()
                    Attendance.insert {
                        it[id] = recordId
                        it[Attendance.studentId] = UUID.fromString(studentId)
                        it[Attendance.courseId] = UUID.fromString(courseId)
                        it[Attendance.date] = date
                        it[Attendance.status] = status
                    }
                    
                    createdRecords.add(recordId)
                    
                    // Create notification for absent students
                    if (status == AttendanceStatus.ABSENT) {
                        val notificationId = UUID.randomUUID()
                        Notifications.insert {
                            it[id] = notificationId
                            it[userId] = UUID.fromString(studentId)
                            it[message] = "You were marked absent for a class"
                            it[type] = NotificationType.ATTENDANCE
                            it[createdAt] = Instant.now()
                        }
                    }
                }
            }

            call.respond(mapOf(
                "message" to "Attendance recorded successfully",
                "records" to createdRecords.map { it.toString() }
            ))
        }

        // GET ATTENDANCE BY COURSE
        get("/course/{courseId}") {
            val courseId = call.parameters["courseId"]
                ?: return@get call.respond(mapOf("error" to "Course ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@get call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@get
            }

            // Optional date filter
            val dateStr = call.request.queryParameters["date"]
            val date = dateStr?.let {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
            }

            val attendanceRecords = transaction {
                val query = if (date != null) {
                    (Attendance innerJoin Users)
                        .select {
                            (Attendance.courseId eq UUID.fromString(courseId)) and
                            (Attendance.date.greaterEq(date)) and
                            (Attendance.date.less(date.plusSeconds(86400)))
                        }
                } else {
                    (Attendance innerJoin Users)
                        .select { Attendance.courseId eq UUID.fromString(courseId) }
                }

                query.map {
                    mapOf(
                        "id" to it[Attendance.id].value.toString(),
                        "student_id" to it[Attendance.studentId].value.toString(),
                        "student_name" to it[Users.name],
                        "date" to it[Attendance.date].toString(),
                        "status" to it[Attendance.status].name
                    )
                }
            }

            call.respond(attendanceRecords)
        }

        // GET ATTENDANCE BY STUDENT
        get("/student/{studentId}") {
            val studentId = call.parameters["studentId"]
                ?: return@get call.respond(mapOf("error" to "Student ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@get call.respond(mapOf("error" to "Requester ID required"))

            // Students can only view their own attendance
            if (requesterId != studentId && !authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@get call.respond(mapOf("error" to "Not authorized to view this student's attendance"))
            }

            val courseId = call.request.queryParameters["course_id"]

            val attendanceRecords = transaction {
                val baseQuery = if (courseId != null) {
                    (Attendance innerJoin Courses)
                        .select {
                            (Attendance.studentId eq UUID.fromString(studentId)) and
                            (Attendance.courseId eq UUID.fromString(courseId))
                        }
                } else {
                    (Attendance innerJoin Courses)
                        .select { Attendance.studentId eq UUID.fromString(studentId) }
                }

                baseQuery.map {
                    mapOf(
                        "id" to it[Attendance.id].value.toString(),
                        "course_id" to it[Attendance.courseId].value.toString(),
                        "course_name" to it[Courses.name],
                        "date" to it[Attendance.date].toString(),
                        "status" to it[Attendance.status].name
                    )
                }
            }

            call.respond(attendanceRecords)
        }

        // UPDATE ATTENDANCE RECORD (Lecturer only)
        put("/{id}") {
            val attendanceId = call.parameters["id"]
                ?: return@put call.respond(mapOf("error" to "Attendance ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@put call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@put
            }

            val request = call.receive<Map<String, String>>()
            val status = request["status"]?.let { AttendanceStatus.valueOf(it.uppercase()) }
                ?: return@put call.respond(mapOf("error" to "Status is required"))

            transaction {
                Attendance.update({ Attendance.id eq UUID.fromString(attendanceId) }) {
                    it[Attendance.status] = status
                }
            }

            call.respond(mapOf("message" to "Attendance record updated successfully"))
        }
    }
}