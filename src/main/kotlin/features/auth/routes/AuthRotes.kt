package features.auth.routes

import common.responses.success
import features.auth.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Configure authentication routes
 */
fun Routing.configureAuthRoutes() {
    val authService = di.AppInjector.authService
    
    route("/auth") {
        // User registration endpoint
        post("/signup") {
            logger.info { "Received signup request" }
            
            val request = call.receive<SignUpRequest>()
            
            val user = authService.signup(request)
            val tokens = authService.generateTokens(user)
            
            call.respond(
                HttpStatusCode.Created, 
                success(
                    AuthResponse(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                        userId = user.id.toString(),
                        name = user.name,
                        email = user.email,
                        role = user.role.name
                    )
                )
            )
        }
        
        // User login endpoint
        post("/login") {
            logger.info { "Received login request" }
            
            val request = call.receive<LoginRequest>()
            
            val (user, tokens) = authService.login(request)
            
            call.respond(
                HttpStatusCode.OK,
                success(
                    AuthResponse(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                        userId = user.id.toString(),
                        name = user.name,
                        email = user.email,
                        role = user.role.name
                    )
                )
            )
        }
        
        // Token refresh endpoint
        post("/refresh") {
            logger.info { "Received token refresh request" }
            
            val request = call.receive<RefreshTokenRequest>()
            
            val (user, tokens) = authService.refreshToken(request.refreshToken)
            
            call.respond(
                HttpStatusCode.OK,
                success(
                    AuthResponse(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken,
                        userId = user.id.toString(),
                        name = user.name,
                        email = user.email,
                        role = user.role.name
                    )
                )
            )
        }
    }
}