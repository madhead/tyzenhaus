package me.madhead.tyzenhaus.repository

import me.madhead.tyzenhaus.entity.dialog.state.DialogState

/**
 * Dialog states repository.
 */
interface DialogStateRepository {
    /**
     * Get a state for a given user in a given group.
     */
    fun get(groupId: Long, userId: Long): DialogState?

    /**
     * Save the entity.
     */
    fun save(entity: DialogState)

    /**
     * Clear dialog state.
     */
    fun delete(groupId: Long, userId: Long)
}
