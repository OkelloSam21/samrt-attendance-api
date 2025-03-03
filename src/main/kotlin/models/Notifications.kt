package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Notifications : UUIDTable("notifications") {
    val userId = reference("user_id", Users.id)
    val message = text("message")
    val type = enumerationByName("type", 15, NotificationType::class)
    val createdAt = timestamp("created_at")
}

enum class NotificationType { ASSIGNMENT, ATTENDANCE, GENERAL }
