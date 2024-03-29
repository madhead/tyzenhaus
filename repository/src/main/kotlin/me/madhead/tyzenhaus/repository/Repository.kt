package me.madhead.tyzenhaus.repository

/**
 * Base repository interface.
 */
interface Repository<ID, T> {
    /**
     * Get the entity by its identifier.
     */
    fun get(id: ID): T?

    /**
     * Save the entity.
     */
    fun save(entity: T)
}
