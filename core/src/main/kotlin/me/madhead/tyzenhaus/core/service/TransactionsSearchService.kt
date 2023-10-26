package me.madhead.tyzenhaus.core.service

import me.madhead.tyzenhaus.entity.transaction.Transaction
import me.madhead.tyzenhaus.repository.TransactionRepository

/**
 * Transaction search parameters.
 *
 * A Group (Chat) ID is not considered a search parameter; it is supplied as a standalone parameter based on a security token.
 *
 * All parameters are optional. If a parameter is omitted, any transaction will be considered a match. Therefore, using a default, empty
 * TransactionsSearchParams object will result in all transactions being returned.
 *
 * @property title The title to search for.
 */
data class TransactionsSearchParams(
    val title: String? = null,
)

/**
 * Search for transactions.
 */
class TransactionsSearchService(
    private val transactionRepository: TransactionRepository,
) {
    /**
     * Lists transactions of the [group], matching the given [searchParams].
     */
    fun search(group: Long, searchParams: TransactionsSearchParams): List<Transaction> {
        return transactionRepository.search(group)
    }
}
