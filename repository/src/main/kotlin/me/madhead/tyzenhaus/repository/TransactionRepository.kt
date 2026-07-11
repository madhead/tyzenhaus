package me.madhead.tyzenhaus.repository

import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.entity.transaction.TransactionsPage
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams

/**
 * Transactions repository.
 */
interface TransactionRepository : Repository<Long, Transaction> {
    /**
     * Retrieves a list of unique currencies used in transactions of the specified group.
     */
    suspend fun groupCurrencies(groupId: Long): List<String>

    /**
     * Retrieves a page of the [group][groupId]'s transactions matching the given [params].
     */
    suspend fun search(groupId: Long, params: TransactionsSearchParams): TransactionsPage
}
