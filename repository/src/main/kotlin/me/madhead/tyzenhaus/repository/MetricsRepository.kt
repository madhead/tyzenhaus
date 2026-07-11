package me.madhead.tyzenhaus.repository

/**
 * Metrics repository.
 */
interface MetricsRepository {
    /**
     * Get total number of chats.
     */
    suspend fun totalNumberOfChats(): Int

    /**
     * Get number of groups with at least one transaction.
     */
    suspend fun numberOfGroupsWithTransactions(): Int

    /**
     * Get total number of transactions.
     */
    suspend fun numberOfTransactions(): Int

    /**
     * Get average group size.
     */
    suspend fun averageGroupSize(): Double
}
