package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : UUIDTable("users") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = text("password")
    val role = enumerationByName("role", 10, UserRole::class)
    val createdAt = timestamp("created_at")
}

enum class UserRole { STUDENT, LECTURER, ADMIN }
