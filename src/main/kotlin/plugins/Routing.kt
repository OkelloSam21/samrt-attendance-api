package plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import routes.*


fun Application.configureRouting() {
       routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "2.0"
        }
        authRoutes()
        userRoutes()
        assignmentRoutes()
        attendanceRoutes()
        courseRoutes()
//        gradeRoutes()
    }
}
