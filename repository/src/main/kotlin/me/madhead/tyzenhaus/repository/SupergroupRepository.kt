package me.madhead.tyzenhaus.repository

/**
 * This repository is used to update the database when doing supergroup updates.
 */
interface SupergroupRepository {
    /**
     * Update the group [from] to the supergroup [to].
     */
    fun update(from: Long, to: Long)
}
