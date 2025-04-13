package features.courses.routes

import common.responses.success
import common.responses.successMessage
import features.courses.models.CreateCourseRequest
import features.courses.models.UpdateCourseRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Configure course management routes
 */
fun Routing.configureCourseRoutes() {
    val courseService = di.AppInjector.courseService
    val roleAuthorization = di.AppInjector.roleAuthorization

    route("/courses") {
        // Public course listing
        get {
            val courses = courseService.getAllCourses()
            call.respond(success(courses))
        }

        // Get course by ID
        get("/{id}") {
            val id = call.parameters["id"]?.let {
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            } ?: throw IllegalArgumentException("Invalid course ID format")

            val course = courseService.getCourseById(id)
            call.respond(success(course))
        }

        // Protected endpoints
        authenticate("auth-jwt") {
            // Create a new course (Lecturers and Admins only)
            post {
                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)

                // Check if user is a lecturer or admin
                if (userRole != domain.models.UserRole.LECTURER && userRole != domain.models.UserRole.ADMIN) {
                    throw common.exceptions.ForbiddenException("Only lecturers and admins can create courses")
                }

                val request = call.receive<CreateCourseRequest>()
                val createdCourse = courseService.createCourse(UUID.fromString(userId), request)

                call.respond(HttpStatusCode.Created, success(createdCourse))
            }

            // Update a course (Owner or Admin only)
            put("/{id}") {
                val id = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid course ID format")

                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)

                val request = call.receive<UpdateCourseRequest>()
                val updatedCourse = courseService.updateCourse(id, UUID.fromString(userId), userRole, request)

                call.respond(success(updatedCourse))
            }

            // Delete a course (Owner or Admin only)
            delete("/{id}") {
                val id = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid course ID format")

                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)

                courseService.deleteCourse(id, UUID.fromString(userId), userRole)

                call.respond(successMessage("Course deleted successfully"))
            }

            // Get courses taught by the current lecturer
            get("/lecturer/me") {
                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)

                // Check if user is a lecturer
                if (userRole != domain.models.UserRole.LECTURER && userRole != domain.models.UserRole.ADMIN) {
                    throw common.exceptions.ForbiddenException("Only lecturers and admins can view their courses")
                }

                val courses = courseService.getCoursesByLecturer(UUID.fromString(userId))

                call.respond(success(courses))
            }

            // Get courses for a specific lecturer
            get("/lecturer/{id}") {
                val lecturerId = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid lecturer ID format")

                val courses = courseService.getCoursesByLecturer(lecturerId)

                call.respond(success(courses))
            }

            // Get courses for the current student
            get("/student/me") {
                val userId = roleAuthorization.getUserId(call)
                roleAuthorization.requireStudent(call)

                val courses = courseService.getCoursesForStudent(UUID.fromString(userId))

                call.respond(success(courses))
            }

            // Get courses for a specific student (Admin only)
            get("/student/{id}") {
                roleAuthorization.requireAdmin(call)

                val studentId = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid student ID format")

                val courses = courseService.getCoursesForStudent(studentId)

                call.respond(success(courses))
            }
        }
    }
}