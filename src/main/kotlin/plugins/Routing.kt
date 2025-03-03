package plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.assignmentRoutes
import routes.attendanceRoutes
import routes.courseRoutes
import routes.userRoutes

fun Application.configureRouting() {
    routing {
        userRoutes()
        assignmentRoutes()
        attendanceRoutes()
        courseRoutes()
//        gradeRoutes()
    }
}
