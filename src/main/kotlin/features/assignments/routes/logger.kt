package features.assignments.routes

import common.responses.success
import common.responses.successMessage
import features.assignments.models.CreateAssignmentRequest
import features.assignments.models.CreateSubmissionRequest
import features.assignments.models.GradeRequest
import features.assignments.models.UpdateAssignmentRequest
import features.assignments.services.AssignmentService
import features.auth.util.RoleAuthorization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Configure assignment routes
 */
fun Routing.configureAssignmentRoutes() {
    val assignmentService = di.AppInjector.assignmentService
    val roleAuthorization = di.AppInjector.roleAuthorization
    
    authenticate("auth-jwt") {
        route("/assignments") {
            // Create a new assignment (Lecturers only)
            post {
                val userId = roleAuthorization.getUserId(call)
                roleAuthorization.requireLecturer(call)
                
                val request = call.receive<CreateAssignmentRequest>()
                val assignment = assignmentService.createAssignment(UUID.fromString(userId), request)
                
                call.respond(HttpStatusCode.Created, success(assignment))
            }
            
            // Get all assignments for a course
            get("/course/{courseId}") {
                val courseId = call.parameters["courseId"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid course ID format")
                
                val assignments = assignmentService.getAssignmentsByCourse(courseId)
                call.respond(success(assignments))
            }
            
            // Get assignments created by the current lecturer
            get("/lecturer/me") {
                val userId = roleAuthorization.getUserId(call)
                roleAuthorization.requireLecturer(call)
                
                val assignments = assignmentService.getAssignmentsByLecturer(UUID.fromString(userId))
                call.respond(success(assignments))
            }
            
            // Get assignments by a specific lecturer
            get("/lecturer/{lecturerId}") {
                val lecturerId = call.parameters["lecturerId"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid lecturer ID format")
                
                val assignments = assignmentService.getAssignmentsByLecturer(lecturerId)
                call.respond(success(assignments))
            }
            
            // Get an assignment by ID
            get("/{id}") {
                val id = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid assignment ID format")
                
                val assignment = assignmentService.getAssignmentById(id)
                call.respond(success(assignment))
            }
            
            // Update an assignment (Owner or Admin only)
            put("/{id}") {
                val id = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid assignment ID format")
                
                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)
                
                val request = call.receive<UpdateAssignmentRequest>()
                val updatedAssignment = assignmentService.updateAssignment(
                    id, 
                    UUID.fromString(userId), 
                    userRole, 
                    request
                )
                
                call.respond(success(updatedAssignment))
            }
            
            // Delete an assignment (Owner or Admin only)
            delete("/{id}") {
                val id = call.parameters["id"]?.let {
                    try {
                        UUID.fromString(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                } ?: throw IllegalArgumentException("Invalid assignment ID format")
                
                val userId = roleAuthorization.getUserId(call)
                val userRole = roleAuthorization.getUserRole(call)
                
                assignmentService.deleteAssignment(id, UUID.fromString(userId), userRole)
                call.respond(successMessage("Assignment deleted successfully"))
            }
            
            // Submissions routes
            route("/submissions") {
                // Submit an assignment (Students only)
                post("/{assignmentId}") {
                    val assignmentId = call.parameters["assignmentId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid assignment ID format")
                    
                    val userId = roleAuthorization.getUserId(call)
                    roleAuthorization.requireStudent(call)
                    
                    val request = call.receive<CreateSubmissionRequest>()
                    val submission = assignmentService.submitAssignment(
                        assignmentId,
                        UUID.fromString(userId),
                        request
                    )
                    
                    call.respond(HttpStatusCode.Created, success(submission))
                }
                
                // Get all submissions for an assignment (Lecturer or Admin only)
                get("/assignment/{assignmentId}") {
                    roleAuthorization.requireAdminOrLecturer(call)
                    
                    val assignmentId = call.parameters["assignmentId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid assignment ID format")
                    
                    val submissions = assignmentService.getSubmissionsByAssignment(assignmentId)
                    call.respond(success(submissions))
                }
                
                // Get submissions by the current student
                get("/student/me") {
                    val userId = roleAuthorization.getUserId(call)
                    roleAuthorization.requireStudent(call)
                    
                    val submissions = assignmentService.getSubmissionsByStudent(UUID.fromString(userId))
                    call.respond(success(submissions))
                }
                
                // Get submissions by a specific student (Lecturer or Admin only)
                get("/student/{studentId}") {
                    roleAuthorization.requireAdminOrLecturer(call)
                    
                    val studentId = call.parameters["studentId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid student ID format")
                    
                    val submissions = assignmentService.getSubmissionsByStudent(studentId)
                    call.respond(success(submissions))
                }
                
                // Get a submission by ID
                get("/{id}") {
                    val id = call.parameters["id"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid submission ID format")
                    
                    val submission = assignmentService.getSubmissionById(id)
                    call.respond(success(submission))
                }
                
                // Grade a submission (Lecturer or Admin only)
                post("/{id}/grade") {
                    val id = call.parameters["id"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid submission ID format")
                    
                    val userId = roleAuthorization.getUserId(call)
                    roleAuthorization.requireAdminOrLecturer(call)
                    
                    val request = call.receive<GradeRequest>()
                    val grade = assignmentService.gradeSubmission(
                        id,
                        request,
                        UUID.fromString(userId)
                    )
                    
                    call.respond(HttpStatusCode.Created, success(grade))
                }
            }
            
            // Grades routes
            route("/grades") {
                // Get all grades for an assignment (Lecturer or Admin only)
                get("/assignment/{assignmentId}") {
                    roleAuthorization.requireAdminOrLecturer(call)
                    
                    val assignmentId = call.parameters["assignmentId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid assignment ID format")
                    
                    val grades = assignmentService.getGradesByAssignment(assignmentId)
                    call.respond(success(grades))
                }
                
                // Get grades for the current student
                get("/student/me") {
                    val userId = roleAuthorization.getUserId(call)
                    roleAuthorization.requireStudent(call)
                    
                    val grades = assignmentService.getGradesByStudent(UUID.fromString(userId))
                    call.respond(success(grades))
                }
                
                // Get grades for a specific student (Lecturer or Admin only)
                get("/student/{studentId}") {
                    roleAuthorization.requireAdminOrLecturer(call)
                    
                    val studentId = call.parameters["studentId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid student ID format")
                    
                    val grades = assignmentService.getGradesByStudent(studentId)
                    call.respond(success(grades))
                }
                
                // Get a grade by ID
                get("/{id}") {
                    val id = call.parameters["id"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    } ?: throw IllegalArgumentException("Invalid grade ID format")
                    
                    val grade = assignmentService.getGradeById(id)
                    call.respond(success(grade))
                }
            }
        }
    }
}