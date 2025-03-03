package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Submissions : UUIDTable("submissions") {
    val studentId = reference("student_id", Users.id)
    val assignmentId = reference("assignment_id", Assignments.id)
    val fileUrl = text("file_url")
    val submissionDate = timestamp("submission_date")
    val grade = float("grade").nullable()
}

