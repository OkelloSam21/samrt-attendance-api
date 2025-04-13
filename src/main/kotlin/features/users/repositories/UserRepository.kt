package features.users.repositories

import domain.models.User
import domain.repositories.BaseRepository
import java.util.UUID

/**
 * User repository interface
 */
interface UserRepository : BaseRepository<User> {
    suspend fun getByEmail(email: String): User?
}