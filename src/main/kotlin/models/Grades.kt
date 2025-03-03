package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Grades : UUIDTable("grades") {
    val studentId = reference("student_id", Users.id)
    val assignmentId = reference("assignment_id", Assignments.id)
    val score = float("score")
    val feedback = text("feedback").nullable()
    val gradedAt = timestamp("graded_at")
}
