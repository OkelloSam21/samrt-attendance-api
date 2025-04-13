package features.attendance.repositories

import domain.models.AttendanceSession
import java.util.*

/**
 * Attendance session repository interface
 */
interface AttendanceSessionRepository {
    suspend fun getById(id: UUID): AttendanceSession?
    suspend fun getBySessionCode(code: String): AttendanceSession?
    suspend fun getActiveSessions(lecturerId: UUID): List<AttendanceSession>
    suspend fun getSessionsByCourse(courseId: UUID): List<AttendanceSession>
    suspend fun create(session: AttendanceSession): AttendanceSession
    suspend fun update(session: AttendanceSession): Boolean
    suspend fun delete(id: UUID): Boolean
}

