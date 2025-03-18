package models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp


// UserRole Enum
enum class UserRole {
    STUDENT, LECTURER, ADMIN
}

object Users : UUIDTable("users") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = text("password") // Store hashed passwords here
    val role = enumerationByName("role", 10, UserRole::class)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").nullable()
}

object Students : Table("students") {
    val userId = reference("user_id", Users.id)
    val regNo = varchar("reg_no", 255).uniqueIndex()
    val department = varchar("department", 100).nullable()
    val yearOfStudy = integer("year_of_study").nullable()

    override val primaryKey = PrimaryKey(userId)

}

object Staff : Table("staff") {
    val userId = reference("user_id", Users.id)
    val employeeId = varchar("employee_id", 255).uniqueIndex()
    val department = varchar("department", 100).nullable()
    val position = varchar("position", 100).nullable()

    override val primaryKey = PrimaryKey(userId)
}