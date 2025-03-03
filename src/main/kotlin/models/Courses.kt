package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Courses : UUIDTable("courses") {
    val name = varchar("name", 255)
    val lecturerId = reference("lecturer_id", Users.id)
    val createdAt = timestamp("created_at")
}

