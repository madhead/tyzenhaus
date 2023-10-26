package me.madhead.tyzenhaus.repository

import me.madhead.tyzenhaus.entity.transaction.Transaction

/**
 * Transactions repository.
 */
interface TransactionRepository : Repository<Long, Transaction> {
    /**
     * Retrieves a list of unique currencies used in transactions of the specified group.
     */
    fun groupCurrencies(groupId: Long): List<String>

    /**
     * Retrieve transactions matching criteria.
     */
    fun search(groupId: Long): List<Transaction>
}
