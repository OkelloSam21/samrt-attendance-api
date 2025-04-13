package features.auth.repositories

import di.dbQuery
import domain.models.User
import domain.models.UserRole
import domain.tables.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository
 */
@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }
    
    /**
     * Convert a ResultRow to User model
     */
    private fun ResultRow.toUser(): User = User(
        id = this[Users.id].value,
        name = this[Users.name],
        email = this[Users.email],
        password = this[Users.password],
        role = this[Users.role],
        createdAt = this[Users.createdAt],
        updatedAt = this[Users.updatedAt]
    )
}