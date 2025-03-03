package routes

import auth.authorize
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Courses
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

fun Route.courseRoutes() {
    route("/courses") {
        // CREATE COURSE (Lecturer and Admin only)
        post {
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@post call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val name = request["name"] ?: return@post call.respond(mapOf("error" to "Course name is required"))
            val lecturerId = request["lecturer_id"] ?: requesterId

            val courseId = UUID.randomUUID()

            transaction {
                Courses.insert {
                    it[id] = courseId
                    it[this.name] = name
                    it[this.lecturerId] = UUID.fromString(lecturerId)
                    it[createdAt] = Instant.now()
                }
            }

            call.respond(mapOf("message" to "Course created successfully", "courseId" to courseId.toString()))
        }

        // GET ALL COURSES
        get {
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@get call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER, UserRole.STUDENT)) {
                return@get
            }

            val courses = transaction {
                (Courses innerJoin Users)
                    .select { Users.id eq Courses.lecturerId }
                    .map {
                        mapOf(
                            "id" to it[Courses.id].value.toString(),
                            "name" to it[Courses.name],
                            "lecturer_id" to it[Courses.lecturerId].value.toString(),
                            "lecturer_name" to it[Users.name],
                            "created_at" to it[Courses.createdAt].toString()
                        )
                    }
            }

            call.respond(courses)
        }

        // GET COURSE BY ID
        get("/{id}") {
            val courseId = call.parameters["id"] ?: return@get call.respond(mapOf("error" to "Course ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@get call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER, UserRole.STUDENT)) {
                return@get
            }

            val course = transaction {
                (Courses innerJoin Users)
                    .select { (Courses.id eq UUID.fromString(courseId)) and (Users.id eq Courses.lecturerId) }
                    .map {
                        mapOf(
                            "id" to it[Courses.id].value.toString(),
                            "name" to it[Courses.name],
                            "lecturer_id" to it[Courses.lecturerId].value.toString(),
                            "lecturer_name" to it[Users.name],
                            "created_at" to it[Courses.createdAt].toString()
                        )
                    }
                    .singleOrNull()
            }

            if (course == null) {
                call.respond(mapOf("error" to "Course not found"))
            } else {
                call.respond(course)
            }
        }

        // UPDATE COURSE (Lecturer who owns the course and Admin only)
        put("/{id}") {
            val courseId = call.parameters["id"] ?: return@put call.respond(mapOf("error" to "Course ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@put call.respond(mapOf("error" to "Requester ID required"))

            // Get the course and check if requester is the lecturer or an admin
            val course = transaction {
                Courses.select { Courses.id eq UUID.fromString(courseId) }.singleOrNull()
            } ?: return@put call.respond(mapOf("error" to "Course not found"))

            val lecturerId = course[Courses.lecturerId].value.toString()
            
            if (!authorize(call, requesterId, UserRole.ADMIN) && requesterId != lecturerId) {
                return@put call.respond(mapOf("error" to "Not authorized to update this course"))
            }

            val request = call.receive<Map<String, String>>()
            val name = request["name"] ?: return@put call.respond(mapOf("error" to "Course name is required"))
            val newLecturerId = request["lecturer_id"] ?: lecturerId

            transaction {
                Courses.update({ Courses.id eq UUID.fromString(courseId) }) {
                    it[this.name] = name
                    it[this.lecturerId] = UUID.fromString(newLecturerId)
                }
            }

            call.respond(mapOf("message" to "Course updated successfully"))
        }

        // DELETE COURSE (Admin only)
        delete("/{id}") {
            val courseId = call.parameters["id"] ?: return@delete call.respond(mapOf("error" to "Course ID is required"))
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@delete call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN)) {
                return@delete
            }

            transaction {
                Courses.deleteWhere { id eq UUID.fromString(courseId) }
            }

            call.respond(mapOf("message" to "Course deleted successfully"))
        }
    }
}