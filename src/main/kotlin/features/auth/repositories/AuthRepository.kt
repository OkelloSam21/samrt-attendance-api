package features.auth.repositories

import domain.models.User
import domain.repositories.BaseRepository
import java.util.UUID

/**
 * Authentication repository interface
 */
interface AuthRepository {
    suspend fun findUserByEmail(email: String): User?
}