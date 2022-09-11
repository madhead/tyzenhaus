package me.madhead.tyzenhaus.repository

/**
 * Metrics repository.
 */
interface MetricsRepository {
    /**
     * Get total number of chats.
     */
    fun totalNumberOfChats(): Int

    /**
     * Get number of groups with at least one transaction.
     */
    fun numberOfGroupsWithTransactions(): Int

    /**
     * Get total number of transactions.
     */
    fun numberOfTransactions(): Int

    /**
     * Get average group size.
     */
    fun averageGroupSize(): Double
}
