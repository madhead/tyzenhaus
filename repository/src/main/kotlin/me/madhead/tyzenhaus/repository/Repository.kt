package me.madhead.tyzenhaus.repository

/**
 * Base repository interface.
 */
interface Repository<ID, T> {
    /**
     * Get the entity by its identifier.
     */
    suspend fun get(id: ID): T?

    /**
     * Save the entity.
     */
    suspend fun save(entity: T)
}
