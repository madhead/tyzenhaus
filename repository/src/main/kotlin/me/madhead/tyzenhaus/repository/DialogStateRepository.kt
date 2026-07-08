package me.madhead.tyzenhaus.repository

import me.madhead.tyzenhaus.entity.dialog.state.DialogState

/**
 * Dialog states repository.
 */
interface DialogStateRepository {
    /**
     * Get a state for a given user in a given group.
     */
    suspend fun get(groupId: Long, userId: Long): DialogState?

    /**
     * Save the entity.
     */
    suspend fun save(entity: DialogState)

    /**
     * Clear dialog state.
     */
    suspend fun delete(groupId: Long, userId: Long)
}
