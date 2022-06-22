package me.madhead.tyzenhaus.repository

/**
 * This repository is used to update the database when doing supergroup updates.
 */
interface SupergroupRepository {
    fun update(from: Long, to: Long)
}
