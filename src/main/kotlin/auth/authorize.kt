package auth

import io.ktor.server.application.*
import models.UserRole
import common.util.authorizeToken

/**
 * Function to verify user authorization, now simplified using `authorizeToken`.
 *
 * @param call The application call containing the request data.
 * @param secret The secret key used to validate the JWT token.
 * @param allowedRoles A variable number of roles allowed for the resource.
 * @return A boolean indicating whether the authorization was successful.
 */
suspend fun authorize(call: ApplicationCall, secret: String, vararg allowedRoles: UserRole): Boolean {
    // Wrap allowedRoles into a Set for compatibility
    val result = authorizeToken(call, secret, allowedRoles.toSet(), checkUserExistence = true)
    return result != null
}