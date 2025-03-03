package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Assignments : UUIDTable("assignments") {
    val courseId = reference("course_id", Courses.id)
    val lecturerId = reference("lecturer_id", Users.id)
    val title = varchar("title", 255)
    val description = text("description")
    val dueDate = timestamp("due_date")
}
