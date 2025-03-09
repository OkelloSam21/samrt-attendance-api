package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : UUIDTable("users") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val regNo = varchar("reg_no", 255).nullable().uniqueIndex()
    val employeeRoleNo = varchar("employee_role_no", 255).nullable().uniqueIndex()
    val password = text("password") // Store hashed passwords here
    val role = enumerationByName("role", 10, UserRole::class)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").nullable()
}

// UserRole Enum
enum class UserRole {
    STUDENT, LECTURER, ADMIN
}
