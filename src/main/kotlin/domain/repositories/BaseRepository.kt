package domain.repositories

import domain.models.BaseEntity
import java.util.UUID

/**
 * Base repository interface with common CRUD operations
 */
interface BaseRepository<T : BaseEntity> {
    suspend fun getById(id: UUID): T?
    suspend fun getAll(): List<T>
    suspend fun create(entity: T): T
    suspend fun update(entity: T): Boolean
    suspend fun delete(id: UUID): Boolean
}