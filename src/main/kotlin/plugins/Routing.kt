package plugins

import di.AppInjector
//import features.assignments.routes.configureAssignmentRoutes
import features.attendance.routes.configureAttendanceRoutes
import features.auth.routes.configureAuthRoutes
import features.courses.routes.configureCourseRoutes
import features.users.routes.configureUserRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Configure routing
 */
fun Application.configureRouting() {
    logger.info { "Configuring routing..." }

    routing {
        // API version and health check
        get("/") {
            call.respond(mapOf(
                "name" to "Smart Attendance API",
                "version" to "1.0.0",
                "status" to "running"
            ))
        }

        // Feature routes
        configureAuthRoutes()
        configureUserRoutes()
        configureCourseRoutes()
        configureAttendanceRoutes()
//        configureAssignmentRoutes()

        // Other routes can be added here
    }

    logger.info { "Routing configuration complete" }
}