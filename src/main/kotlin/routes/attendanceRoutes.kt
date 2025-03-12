package routes

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import models.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import util.*
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Route.attendanceRoutes() {
    authenticate("auth-jwt") {
        route("/attendance") {
            // Create an attendance session (Lecturer only)
            post("/sessions") {
                val requester = authorizeToken(call,SECRET, setOf(UserRole.LECTURER))
                    ?: return@post
                val (requesterId, _) = requester // Only need the userId here

                // Parse the request body
                val request = call.parseAndValidateRequest<AttendanceSessionRequest>(
                    validate = {
                        val errors = mutableListOf<String>()
                        if (courseId.isBlank()) errors.add("Course ID cannot be empty.")
                        if (durationMinutes <= 0) errors.add("Duration must be greater than 0.")
                        if (geoFence.latitude !in -90.0..90.0) errors.add("Latitude must be between -90 and 90.")
                        if (geoFence.longitude !in -180.0..180.0) errors.add("Longitude must be between -180 and 180.")
                        if (geoFence.radiusMeters <= 0) errors.add("GeoFence radius must be greater than 0.")
                        errors.isEmpty()
                    }, errorMessage = "Invalid request body format"
                ) ?: return@post

                // Parse the session type safely
                val sessionType = try {
                    SessionType.valueOf(request.sessionType.uppercase())
                } catch (e: IllegalArgumentException) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid session type")
                    )
                }

                // Extract fields from the request
                val courseId = request.courseId
                val durationMinutes = request.durationMinutes
                val latitude = request.geoFence.latitude
                val longitude = request.geoFence.longitude
                val radiusMeters = request.geoFence.radiusMeters

                // Generate session details
                val sessionCode = generateSessionCode()
                val sessionId = UUID.randomUUID()
                val now = Instant.now()
                val expiresAt = now.plus(durationMinutes.toLong(), ChronoUnit.MINUTES)

                // Insert session into the database
                transaction {
                    AttendanceSessions.insert {
                        it[id] = sessionId
                        it[AttendanceSessions.courseId] = UUID.fromString(courseId)
                        it[lecturerId] = UUID.fromString(requesterId)
                        it[AttendanceSessions.sessionCode] = sessionCode
                        it[AttendanceSessions.sessionType] = sessionType
                        it[createdAt] = now
                        it[AttendanceSessions.expiresAt] = expiresAt
                        it[AttendanceSessions.latitude] = latitude
                        it[AttendanceSessions.longitude] = longitude
                        it[AttendanceSessions.radiusMeters] = radiusMeters
                    }
                }

                // Respond with session details
                call.respond(
                    HttpStatusCode.Created, mapOf(
                        "message" to "Session created successfully",
                        "session_id" to sessionId.toString(),
                        "session_code" to sessionCode,
                        "expires_at" to expiresAt.toString()
                    )
                )
            }

            // Generate QR Code for an attendance session
            get("/sessions/qr") {
                // Authenticate the user and ensure they are authorized
                val userId = call.authorizeUser(SECRET, UserRole.LECTURER)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized access"))

                // Fetch the session associated with the lecturer
                val session = transaction {
                    AttendanceSessions.select { AttendanceSessions.lecturerId eq UUID.fromString(userId) }
                        .orderBy(AttendanceSessions.createdAt, SortOrder.DESC) // Fetch the latest session
                        .limit(1)
                        .singleOrNull()
                } ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "No active session found"))

                // Generate the QR code using the session code (without exposing session ID to the user)
                val qrCodeImage = generateQRCode(session[AttendanceSessions.sessionCode])

                // Respond with the generated QR code image
                call.respond(ByteArrayContent(qrCodeImage, ContentType.Image.PNG))
            }

            // Mark attendance (Students)
            post("/mark") {
                val requester = authorizeToken(call,SECRET, setOf(UserRole.STUDENT))
                    ?: return@post
                val (studentId, _) = requester // Only need the userId here

                // Extract the session code and location (optional) from the request body
                val request = call.receive<Map<String, Any>>()
                val sessionCode = request["session_code"]?.toString()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Session code is required"))
                val location = request["location"] as? Map<*, *>
                val studentLatitude = location?.get("latitude")?.toString()?.toDouble()
                val studentLongitude = location?.get("longitude")?.toString()?.toDouble()

                val session = transaction {
                    AttendanceSessions
                        .select { AttendanceSessions.sessionCode eq sessionCode }
                        .singleOrNull()
                } ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("error" to "Session not found"))

                val now = Instant.now()
                if (now.isAfter(session[AttendanceSessions.expiresAt])) {
                    return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Session has expired"))
                }

                if (session[AttendanceSessions.sessionType] == SessionType.PHYSICAL) {
                    val radius = session[AttendanceSessions.radiusMeters]
                    val lecturerLatitude = session[AttendanceSessions.latitude]
                    val lecturerLongitude = session[AttendanceSessions.longitude]

                    if (radius != null && lecturerLatitude != null && lecturerLongitude != null) {
                        val withinRadius = checkIfWithinRadius(
                            studentLatitude,
                            studentLongitude,
                            lecturerLatitude,
                            lecturerLongitude,
                            radius
                        )
                        if (!withinRadius) {
                            return@post call.respond(
                                HttpStatusCode.BadRequest,
                                mapOf("error" to "Outside allowed radius")
                            )
                        }
                    }
                }

                // Mark attendance
                transaction {
                    Attendance.insert {
                        it[Attendance.studentId] = UUID.fromString(studentId)
                        it[courseId] = session[AttendanceSessions.courseId]
                        it[sessionId] = session[AttendanceSessions.id]
                        it[date] = now
                        it[status] = AttendanceStatus.PRESENT
                        it[verificationMethod] = VerificationMethod.QR_CODE // Or other methods based on implementation
                        it[locationLatitude] = studentLatitude
                        it[locationLongitude] = studentLongitude
                    }
                }

                call.respond(HttpStatusCode.OK, mapOf("message" to "Attendance marked successfully"))
            }
        }
    }
}

// Function to check if coordinates are within a radius
fun checkIfWithinRadius(
    studentLat: Double?,
    studentLong: Double?,
    lecturerLat: Double,
    lecturerLong: Double,
    radius: Double
): Boolean {
    if (studentLat == null || studentLong == null) {
        return false
    }
    val earthRadius = 6371000.0 // In meters
    val dLat = Math.toRadians(lecturerLat - studentLat)
    val dLong = Math.toRadians(lecturerLong - studentLong)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(studentLat)) * cos(Math.toRadians(lecturerLat)) *
            sin(dLong / 2) * sin(dLong / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c
    return distance <= radius
}


fun generateQRCode(sessionCode: String, width: Int = 250, height: Int = 250): ByteArray {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(sessionCode, BarcodeFormat.QR_CODE, width, height)
    val outputStream = ByteArrayOutputStream()
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
    return outputStream.toByteArray()
}


@Serializable
data class AttendanceSessionRequest(
    @SerialName("course_id") val courseId: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("session_type") val sessionType: String,
    @SerialName("geo_fence") val geoFence: GeoFence
)

@Serializable
data class GeoFence(
    val latitude: Double,
    val longitude: Double,
    @SerialName("radius_meters") val radiusMeters: Double // Fix applied here
)
