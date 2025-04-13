package features.assignments.repositories

import domain.models.Assignment
import domain.repositories.BaseRepository
import java.util.UUID

/**
 * Assignment repository interface
 */
interface AssignmentRepository : BaseRepository<Assignment> {
    suspend fun getAssignmentsByCourse(courseId: UUID): List<Assignment>
    suspend fun getAssignmentsByLecturer(lecturerId: UUID): List<Assignment>
}