package me.madhead.tyzenhaus.core.service

import me.madhead.tyzenhaus.entity.transaction.TransactionsPage
import me.madhead.tyzenhaus.entity.transaction.TransactionsSearchParams
import me.madhead.tyzenhaus.repository.TransactionRepository

/**
 * Search for transactions.
 */
class TransactionsSearchService(
    private val transactionRepository: TransactionRepository,
) {
    /**
     * Lists transactions of the [group], matching the given [params], newest first.
     */
    suspend fun search(group: Long, params: TransactionsSearchParams): TransactionsPage {
        return transactionRepository.search(group, params)
    }
}
