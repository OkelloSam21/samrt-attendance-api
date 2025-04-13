package features.courses.repositories

import domain.models.Course
import domain.repositories.BaseRepository
import java.util.UUID

/**
 * Course repository interface
 */
interface CourseRepository : BaseRepository<Course> {
    suspend fun getCoursesByLecturerId(lecturerId: UUID): List<Course>
    suspend fun getCoursesForStudent(studentId: UUID): List<Course>
}