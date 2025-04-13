package routes

import auth.authorize
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import models.Courses
import models.UserRole
import models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import common.util.SECRET
import common.util.authorizeToken
import common.util.authorizeUser
import common.util.parseAndValidateRequest
import java.time.Instant
import java.util.*

@Serializable
data class CourseRequest(
    val name: String,
//    val lecturerId: String
)

@Serializable
data class CourseResponse(
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: String
)

fun Route.courseRoutes() {
    route("/courses") {
        authenticate("auth-jwt") {
            // CREATE COURSE (Lecturer and Admin only)
            post("/create") {
                val requesterId = call.authorizeUser(SECRET, UserRole.LECTURER) ?: return@post

                val createCourseRequest = call.parseAndValidateRequest<CourseRequest>(
                    validate = { name.isNotBlank() },
                    errorMessage = "Course name is required"
                ) ?: return@post

                val courseId = UUID.randomUUID()

                newSuspendedTransaction {
                    val lecturerExists = Users.select { Users.id eq UUID.fromString(requesterId) }.count() > 0
                    if (!lecturerExists) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid lecturer ID")
                        return@newSuspendedTransaction
                    }

                    Courses.insert {
                        it[id] = courseId
                        it[name] = createCourseRequest.name
                        it[this.lecturerId] = UUID.fromString(requesterId)
                        it[createdAt] = Instant.now()
                    }
                }

                call.respond(
                    HttpStatusCode.Created,
                    mapOf("message" to "Course created successfully", "courseId" to courseId.toString())
                )
            }

            // GET ALL COURSES
            get {
                // Check authorization for Admin, Lecturer, or Student
                if (!authorize(call, SECRET, UserRole.ADMIN, UserRole.LECTURER, UserRole.STUDENT)) {
                    return@get
                }

                val courses = transaction {
                    (Courses innerJoin Users)
                        .select { Users.id eq Courses.lecturerId }
                        .map {
                            CourseResponse(
                                id = it[Courses.id].value.toString(),
                                name = it[Courses.name],
                                lecturerId = it[Courses.lecturerId].value.toString(),
                                lecturerName = it[Users.name],
                                createdAt = it[Courses.createdAt].toString()
                            )
                        }
                }

                call.respond(HttpStatusCode.OK, courses)
            }

            // GET COURSE BY ID
            get("/{id}") {
                if (!authorize(call, SECRET, UserRole.ADMIN, UserRole.LECTURER, UserRole.STUDENT)) {
                    return@get
                }

                val courseId = call.parameters["id"]
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Course ID is required")
                    )

                val course = transaction {
                    (Courses innerJoin Users)
                        .select { (Courses.id eq UUID.fromString(courseId)) and (Users.id eq Courses.lecturerId) }
                        .map {
                            CourseResponse(
                                id = it[Courses.id].value.toString(),
                                name = it[Courses.name],
                                lecturerId = it[Courses.lecturerId].value.toString(),
                                lecturerName = it[Users.name],
                                createdAt = it[Courses.createdAt].toString()
                            )
                        }.singleOrNull()
                }

                if (course == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Course not found"))
                } else {
                    call.respond(HttpStatusCode.OK, course)
                }
            }

            // UPDATE COURSE (Lecturer who owns the course and Admin only)
            put("/update/{id}") {
                val requester = authorizeToken(call,SECRET, setOf(UserRole.ADMIN, UserRole.LECTURER))
                    ?: return@put
                val (requesterId, requesterRole) = requester // Destructure the pair

                val courseId = call.parameters["id"]
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Course ID is required")
                    )

                val course = transaction {
                    Courses.select { Courses.id eq UUID.fromString(courseId) }.singleOrNull()
                } ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Course not found"))

                val lecturerId = course[Courses.lecturerId].value.toString()

                if (requesterRole != UserRole.ADMIN && requesterId != lecturerId) {
                    return@put call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Not authorized to update this course"))
                }

                val updateCourseRequest = call.parseAndValidateRequest<CourseRequest>(
                    validate = { name.isNotBlank() },
                    errorMessage = "Course name is required"
                ) ?: return@put

                transaction {
                    Courses.update({ Courses.id eq UUID.fromString(courseId) }) {
                        it[name] = updateCourseRequest.name
                    }
                }

                call.respond(HttpStatusCode.OK, mapOf("message" to "Course updated successfully"))
            }

            // DELETE COURSE (Admin only)
            delete("/delete/{id}") {
                call.authorizeUser(SECRET, UserRole.ADMIN) ?: return@delete

                val courseId = call.parameters["id"]
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Course ID is required")
                    )

                newSuspendedTransaction {
                    val deletedRows = Courses.deleteWhere { id eq UUID.fromString(courseId) }
                    if (deletedRows > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Course deleted successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Course not found"))
                    }
                }
            }
        }
    }
}