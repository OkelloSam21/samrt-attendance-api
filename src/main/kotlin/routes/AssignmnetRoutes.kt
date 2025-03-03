package routes

import auth.authorize
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.Assignments
import models.UserRole
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

fun Route.assignmentRoutes() {
    route("/assignments") {

        // CREATE ASSIGNMENT (Lecturer only)
        post {
            val requesterId = call.request.queryParameters["requester_id"]
                ?: return@post call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@post
            }

            val request = call.receive<Map<String, String>>()
            val courseId = request["course_id"] ?: return@post call.respond(mapOf("error" to "Course ID is required"))
            val title = request["title"] ?: return@post call.respond(mapOf("error" to "Title is required"))
            val description = request["description"] ?: return@post call.respond(mapOf("error" to "Description is required"))
            val dueDateStr = request["due_date"] ?: return@post call.respond(mapOf("error" to "Due date is required"))

            val dueDate = try {
                Instant.parse(dueDateStr)
            } catch (e: Exception) {
                return@post call.respond(mapOf("error" to "Invalid date format. Use ISO-8601 format."))
            }

            val assignmentId = UUID.randomUUID()

            transaction {
                Assignments.insert {
                    it[id] = assignmentId
                    it[Assignments.courseId] = UUID.fromString(courseId)
                    it[Assignments.lecturerId] = UUID.fromString(requesterId)
                    it[Assignments.title] = title
                    it[Assignments.description] = description
                    it[Assignments.dueDate] = dueDate
                }
            }

            call.respond(mapOf("message" to "Assignment created successfully", "assignmentId" to assignmentId))
        }

        // GET ALL ASSIGNMENTS
        get {
            val assignments = transaction {
                Assignments.selectAll().map {
                    mapOf(
                        "id" to it[Assignments.id].toString(),
                        "course_id" to it[Assignments.courseId].toString(),
                        "title" to it[Assignments.title],
                        "description" to it[Assignments.description],
                        "due_date" to it[Assignments.dueDate].toString()
                    )
                }
            }
            call.respond(assignments)
        }

        // GET SINGLE ASSIGNMENT
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(mapOf("error" to "ID required"))

            val assignment = transaction {
                Assignments.select { Assignments.id eq UUID.fromString(id) }
                    .map {
                        mapOf(
                            "id" to it[Assignments.id].toString(),
                            "course_id" to it[Assignments.courseId].toString(),
                            "title" to it[Assignments.title],
                            "description" to it[Assignments.description],
                            "due_date" to it[Assignments.dueDate].toString()
                        )
                    }.singleOrNull()
            }
            if (assignment != null) call.respond(assignment) else call.respond(mapOf("error" to "Assignment not found"))
        }

        // UPDATE ASSIGNMENT (Lecturer only)
        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(mapOf("error" to "ID required"))
            val requesterId = call.request.queryParameters["requester_id"] ?: return@put call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@put
            }

            val request = call.receive<Map<String, String>>()
            val title = request["title"]
            val description = request["description"]
            val dueDateStr = request["due_date"]

            transaction {
                Assignments.update({ Assignments.id eq UUID.fromString(id) }) {
                    if (title != null) it[Assignments.title] = title
                    if (description != null) it[Assignments.description] = description
                    if (dueDateStr != null) {
                        it[Assignments.dueDate] = Instant.parse(dueDateStr)
                    }
                }
            }
            call.respond(mapOf("message" to "Assignment updated successfully"))
        }

        // DELETE ASSIGNMENT (Lecturer only)
        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(mapOf("error" to "ID required"))
            val requesterId = call.request.queryParameters["requester_id"] ?: return@delete call.respond(mapOf("error" to "Requester ID required"))

            if (!authorize(call, requesterId, UserRole.ADMIN, UserRole.LECTURER)) {
                return@delete
            }

            transaction {
                Assignments.deleteWhere { Assignments.id eq UUID.fromString(id) }
            }
            call.respond(mapOf("message" to "Assignment deleted successfully"))
        }
    }
}